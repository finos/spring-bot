package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.util.List;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.workflow.ChatWorkflowConfig;
import org.finos.symphony.toolkit.workflow.actions.consumers.ActionConsumer;
import org.finos.symphony.toolkit.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerTypeConverterConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.TypeConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistoryImpl;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLParser;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLWriter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.PresentationMLHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRoomsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@AutoConfigureBefore(SharedStreamSingleBotConfig.class)
@Import({ChatWorkflowConfig.class, FreemarkerTypeConverterConfig.class})
public class SymphonyWorkflowConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyWorkflowConfig.class);
	
	@Autowired
	@Qualifier(SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
	SymphonyIdentity botIdentity;
	
	@Autowired
	UsersApi usersApi;
	
	@Autowired
	MessagesApi messagesApi; 
	
	@Autowired
	RoomMembershipApi roomMembershipApi;
	
	@Autowired
	StreamsApi streamsApi;

	@Autowired
	Validator validator;
	
	@Autowired
	AttachmentHandler attachmentHandler;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	@Lazy
	List<TypeConverter> converters;
	
	
	@Bean
	@ConditionalOnMissingBean
	public MessageMLParser simpleMessageParser() {
		return new MessageMLParser();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessageMLWriter messageMLWriter() {
		MessageMLWriter out = new MessageMLWriter();
		out.add(Message.class, out.new OrderedTagWriter("messageML"));
		out.add(Paragraph.class, out.new OrderedTagWriter("p"));
		out.add(OrderedList.class, out.new OrderedTagWriter("ol"));
		out.add(UnorderedList.class, out.new OrderedTagWriter("ul"));
		out.add(CodeBlock.class, out.new SimpleTagWriter("code"));
		out.add(Word.class, out.new PlainWriter());
		
		// table
		// tags
		
		return out;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyResponseHandler symphonyResponseHandler() {
		return new SymphonyResponseHandler(messagesApi, formMessageMLConverter(), 
				messageMLWriter(), entityJsonConverter(), attachmentHandler, resourceLoader);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public FormMessageMLConverter formMessageMLConverter() {
		LOG.info("Setting up Freemarker formMessageMLConverter with {} converters", converters.size());
		return new FreemarkerFormMessageMLConverter(converters);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyHistory symphonyHistory() {
		return new SymphonyHistoryImpl(entityJsonConverter(), messagesApi);
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyRooms symphonyRooms() {
		return new SymphonyRoomsImpl(roomMembershipApi, streamsApi, usersApi);
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter entityJsonConverter() {
		return new EntityJsonConverter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PresentationMLHandler presentationMLHandler(List<ActionConsumer> messageConsumers) {
		return new PresentationMLHandler(simpleMessageParser(), entityJsonConverter(), messageConsumers, symphonyRooms(), botIdentity);
	}

	@Bean
	@ConditionalOnMissingBean
	public FormConverter formConverter() {
		return new FormConverter(symphonyRooms());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public InRoomAddressingChecker inRoomAddressingChecker() {
		UserV2 user = usersApi.v2UserGet(null, null, botIdentity.getEmail(), null, true);
		SymphonyUser su = new SymphonyUser(user.getId(), user.getDisplayName(), user.getEmailAddress(), () -> "");
		return new InRoomAddressingChecker(su, true);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ElementsHandler elementsHandler(List<ActionConsumer> elementsConsumers) {
		return new ElementsHandler(messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
	}

}