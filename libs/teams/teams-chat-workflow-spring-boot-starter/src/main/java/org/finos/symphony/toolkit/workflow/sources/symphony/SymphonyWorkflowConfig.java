package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.json.VersionSpace;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.symphony.toolkit.workflow.ChatWorkflowConfig;
import org.finos.symphony.toolkit.workflow.actions.consumers.ActionConsumer;
import org.finos.symphony.toolkit.workflow.actions.consumers.AddressingChecker;
import org.finos.symphony.toolkit.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.CashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.RoomName;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversations;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversationsImpl;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.HeaderTagResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerTypeConverterConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.TypeConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.jersey.JerseyAttachmentHandlerConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistoryImpl;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLParser;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLWriter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.PresentationMLHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.symphonyoss.fin.Security;
import org.symphonyoss.fin.security.id.Cusip;
import org.symphonyoss.fin.security.id.Isin;
import org.symphonyoss.fin.security.id.Openfigi;
import org.symphonyoss.fin.security.id.Ticker;
import org.symphonyoss.taxonomy.Hashtag;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;
import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
import com.symphony.user.Mention;
import com.symphony.user.StreamID;
import com.symphony.user.UserId;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@AutoConfigureBefore(SharedStreamSingleBotConfig.class)
@Import({ChatWorkflowConfig.class, FreemarkerTypeConverterConfig.class, JerseyAttachmentHandlerConfig.class})
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
	
	@Autowired
	ApplicationContext ac;
	
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
		out.add(OrderedList.class, out.new OrderedTagWriter("ol", out.new OrderedTagWriter("li")));
		out.add(UnorderedList.class, out.new OrderedTagWriter("ul", out.new OrderedTagWriter("li")));
		out.add(CodeBlock.class, out.new SimpleTagWriter("code"));
		out.add(Word.class, out.new PlainWriter());
		out.add(Table.class, out.new TableWriter());
		// tags
		
		return out;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyResponseHandler symphonyResponseHandler() {
		return new SymphonyResponseHandler(messagesApi, streamsApi, usersApi, 
				formMessageMLConverter(), 
				messageMLWriter(), 
				entityJsonConverter(), 
				attachmentHandler, 
				resourceLoader);
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
		return new SymphonyHistoryImpl(entityJsonConverter(), messagesApi, streamsApi, usersApi);
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyConversations symphonyRooms() {
		return new SymphonyConversationsImpl(roomMembershipApi, streamsApi, usersApi, botIdentity);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public HeaderTagResponseHandler headerTagResponsehandler() {
		return new HeaderTagResponseHandler();
	} 
	
	
	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter entityJsonConverter() {
		List<VersionSpace> workAnnotatedversionSpaces = scanForWorkClasses();
		
		List<VersionSpace> chatWorkflowVersionSpaces = Arrays.asList(
			new VersionSpace(EntityJson.getSymphonyTypeName(Security.class), CashTag.class,  "1.0", "0.*"),
			new VersionSpace(EntityJson.getSymphonyTypeName(Hashtag.class), HashTag.class, "1.0", "0.*"),
			new VersionSpace(EntityJson.getSymphonyTypeName(Mention.class), SymphonyUser.class, "1.0"), 
			new VersionSpace(EntityJson.getSymphonyTypeName(Chat.class), SymphonyRoom.class, "1.0"), 
			
			new VersionSpace(UserId.class, "1.0"), 
			new VersionSpace(DisplayName.class, "1.0"), 
			new VersionSpace(RoomName.class, "1.0"), 
			new VersionSpace(StreamID.class, "1.0"), 
			new VersionSpace(EmailAddress.class, "1.0"), 
			ObjectMapperFactory.noVersion(Ticker.class), 
			ObjectMapperFactory.noVersion(Cusip.class), 
			ObjectMapperFactory.noVersion(Isin.class), 
			ObjectMapperFactory.noVersion(Openfigi.class),

			LogMessage.VERSION_SPACE, 
			RoomWelcomeEventConsumer.VERSION_SPACE);
		
		List<VersionSpace> combined = new ArrayList<>();
		combined.addAll(chatWorkflowVersionSpaces);
		combined.addAll(workAnnotatedversionSpaces);
				
		return new EntityJsonConverter(combined);
	}

	protected List<VersionSpace> scanForWorkClasses() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Work.class));
		Set<BeanDefinition> toAdd = scanner.findCandidateComponents(getPackageName(ChatWorkflowConfig.class));
		
		for (String ent : ac.getBeanNamesForAnnotation(SpringBootApplication.class)) {
			String packageName = getPackageName(ac.getBean(ent).getClass());
			Set<BeanDefinition> user = scanner.findCandidateComponents(packageName);
			toAdd.addAll(user);
		}
		
		List<VersionSpace> versionSpaces = toAdd.stream()
			.map(bd -> bd.getBeanClassName()) 
			.map(s -> {
				try {
					return Class.forName(s);
				} catch (ClassNotFoundException e) {
					LOG.error("Couldn't instantiate: "+s, e);
					return null;
				}
			})
			.filter(x -> x != null) 
			.flatMap(c -> {
				Work w = c.getAnnotation(Work.class);
				String jsonTypeName[] = w.jsonTypeName();
				return IntStream.range(0, jsonTypeName.length)
						.mapToObj(i -> {
							String t = jsonTypeName[i];
							if (i == 0) {
								t = StringUtils.hasText(t) ? t : EntityJson.getSymphonyTypeName(c);
								String writeVersion = w.writeVersion();
								String[] readVersions = w.readVersions();
								return new VersionSpace(t, c, writeVersion, readVersions);
							} else {
								String[] readVersions = w.readVersions();
								return new VersionSpace(t, c, null, readVersions);
							}
						});
				})
			.collect(Collectors.toList());
		return versionSpaces;
	}

	protected String getPackageName(Class<?> c) {
		String cn = c.getName();
        int dot = cn.lastIndexOf('.');
        String pn = (dot != -1) ? cn.substring(0, dot).intern() : "";
        return pn;
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
	public AddressingChecker defaultAddressingChecker() {
		UserV2 symphonyBotUser = usersApi.v2UserGet(null, null, botIdentity.getEmail(), null, true);
		SymphonyUser su = new SymphonyUser(symphonyBotUser.getDisplayName(), symphonyBotUser.getEmailAddress());
		return new InRoomAddressingChecker(su, true);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ElementsHandler elementsHandler(List<ActionConsumer> elementsConsumers) {
		return new ElementsHandler(messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
	}

}