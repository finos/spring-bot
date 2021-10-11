package org.finos.springbot.sources.teams;

import java.util.List;

import org.finos.springbot.sources.teams.conversations.TeamsConversations;
import org.finos.springbot.sources.teams.conversations.TeamsConversationsImpl;
import org.finos.springbot.sources.teams.messages.MessageActivityHandler;
import org.finos.symphony.toolkit.workflow.ChatWorkflowConfig;
import org.finos.symphony.toolkit.workflow.actions.consumers.ActionConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.microsoft.bot.integration.spring.BotController;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@Import({ChatWorkflowConfig.class, BotController.class})
public class TeamsWorkflowConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsWorkflowConfig.class);
	
	@Autowired
	Validator validator;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ApplicationContext ac;
	
//
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public SymphonyResponseHandler symphonyResponseHandler() {
//		return new SymphonyResponseHandler(messagesApi, streamsApi, usersApi, 
//				formMessageMLConverter(), 
//				messageMLWriter(), 
//				entityJsonConverter(), 
//				attachmentHandler, 
//				resourceLoader);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public FormMessageMLConverter formMessageMLConverter() {
//		LOG.info("Setting up Freemarker formMessageMLConverter with {} converters", converters.size());
//		return new FreemarkerFormMessageMLConverter(converters);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public SymphonyHistory symphonyHistory() {
//		return new SymphonyHistoryImpl(entityJsonConverter(), messagesApi, streamsApi, usersApi);
//	}
	
	@Bean 
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations() {
		return new TeamsConversationsImpl();
	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public HeaderTagResponseHandler headerTagResponsehandler() {
//		return new HeaderTagResponseHandler();
//	} 
//	
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public EntityJsonConverter entityJsonConverter() {
//		List<VersionSpace> workAnnotatedversionSpaces = scanForWorkClasses();
//		
//		List<VersionSpace> chatWorkflowVersionSpaces = Arrays.asList(
//			new VersionSpace(EntityJson.getSymphonyTypeName(Security.class), CashTag.class,  "1.0", "0.*"),
//			new VersionSpace(EntityJson.getSymphonyTypeName(Hashtag.class), HashTag.class, "1.0", "0.*"),
//			new VersionSpace(EntityJson.getSymphonyTypeName(Mention.class), SymphonyUser.class, "1.0"), 
//			new VersionSpace(EntityJson.getSymphonyTypeName(Chat.class), SymphonyRoom.class, "1.0"), 
//			
//			new VersionSpace(UserId.class, "1.0"), 
//			new VersionSpace(DisplayName.class, "1.0"), 
//			new VersionSpace(RoomName.class, "1.0"), 
//			new VersionSpace(StreamID.class, "1.0"), 
//			new VersionSpace(EmailAddress.class, "1.0"), 
//			ObjectMapperFactory.noVersion(Ticker.class), 
//			ObjectMapperFactory.noVersion(Cusip.class), 
//			ObjectMapperFactory.noVersion(Isin.class), 
//			ObjectMapperFactory.noVersion(Openfigi.class),
//
//			LogMessage.VERSION_SPACE, 
//			RoomWelcomeEventConsumer.VERSION_SPACE);
//		
//		List<VersionSpace> combined = new ArrayList<>();
//		combined.addAll(chatWorkflowVersionSpaces);
//		combined.addAll(workAnnotatedversionSpaces);
//				
//		return new EntityJsonConverter(combined);
//	}
//
//	protected List<VersionSpace> scanForWorkClasses() {
//		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
//		scanner.addIncludeFilter(new AnnotationTypeFilter(Work.class));
//		Set<BeanDefinition> toAdd = scanner.findCandidateComponents(getPackageName(ChatWorkflowConfig.class));
//		
//		for (String ent : ac.getBeanNamesForAnnotation(SpringBootApplication.class)) {
//			String packageName = getPackageName(ac.getBean(ent).getClass());
//			Set<BeanDefinition> user = scanner.findCandidateComponents(packageName);
//			toAdd.addAll(user);
//		}
//		
//		List<VersionSpace> versionSpaces = toAdd.stream()
//			.map(bd -> bd.getBeanClassName()) 
//			.map(s -> {
//				try {
//					return Class.forName(s);
//				} catch (ClassNotFoundException e) {
//					LOG.error("Couldn't instantiate: "+s, e);
//					return null;
//				}
//			})
//			.filter(x -> x != null) 
//			.flatMap(c -> {
//				Work w = c.getAnnotation(Work.class);
//				String jsonTypeName[] = w.jsonTypeName();
//				return IntStream.range(0, jsonTypeName.length)
//						.mapToObj(i -> {
//							String t = jsonTypeName[i];
//							if (i == 0) {
//								t = StringUtils.hasText(t) ? t : EntityJson.getSymphonyTypeName(c);
//								String writeVersion = w.writeVersion();
//								String[] readVersions = w.readVersions();
//								return new VersionSpace(t, c, writeVersion, readVersions);
//							} else {
//								String[] readVersions = w.readVersions();
//								return new VersionSpace(t, c, null, readVersions);
//							}
//						});
//				})
//			.collect(Collectors.toList());
//		return versionSpaces;
//	}
//
//	protected String getPackageName(Class<?> c) {
//		String cn = c.getName();
//        int dot = cn.lastIndexOf('.');
//        String pn = (dot != -1) ? cn.substring(0, dot).intern() : "";
//        return pn;
//	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessageActivityHandler messsageActivityHandler(List<ActionConsumer> messageConsumers) {
		return new MessageActivityHandler(messageConsumers, teamsConversations());
	}

//	@Bean
//	@ConditionalOnMissingBean
//	public FormConverter formConverter() {
//		return new FormConverter(symphonyRooms());
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public AddressingChecker defaultAddressingChecker() {
//		UserV2 symphonyBotUser = usersApi.v2UserGet(null, null, botIdentity.getEmail(), null, true);
//		TeamsUser su = new TeamsUser(symphonyBotUser.getDisplayName(), symphonyBotUser.getEmailAddress());
//		return new InRoomAddressingChecker(su, true);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public ElementsHandler elementsHandler(List<ActionConsumer> elementsConsumers) {
//		return new ElementsHandler(messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
//	}

}