package org.finos.springbot.symphony.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.symphony.content.CashTag;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.RoomName;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.symphonyoss.Taxonomy;
import org.symphonyoss.fin.Security;
import org.symphonyoss.fin.security.id.Cusip;
import org.symphonyoss.fin.security.id.Isin;
import org.symphonyoss.fin.security.id.Openfigi;
import org.symphonyoss.fin.security.id.Ticker;
import org.symphonyoss.taxonomy.Hashtag;

import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
import com.symphony.user.Mention;
import com.symphony.user.StreamID;
import com.symphony.user.UserId;

@Configuration
public class DataHandlerCofig {

	private static final Logger LOG = LoggerFactory.getLogger(DataHandlerCofig.class);

	@Autowired
	ApplicationContext ac;

	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter entityJsonConverter() {
		List<VersionSpace> workAnnotatedversionSpaces = scanForWorkClasses();
		
		List<VersionSpace> chatWorkflowVersionSpaces = Arrays.asList(
			new VersionSpace(EntityJson.getEntityJsonTypeNamer(Security.class), CashTag.class,  "1.0", "0.*"),
			new VersionSpace(EntityJson.getEntityJsonTypeNamer(Hashtag.class), HashTag.class, "1.0", "0.*"),
			new VersionSpace(EntityJson.getEntityJsonTypeNamer(Mention.class), SymphonyUser.class, "1.0"), 
			new VersionSpace(EntityJson.getEntityJsonTypeNamer(Chat.class), SymphonyRoom.class, "1.0"), 
			
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
								t = StringUtils.hasText(t) ? t : EntityJson.getEntityJsonTypeNamer(c);
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

	/**
	 * The Symphony client itself uses classes like Isin, Ticker, Mention, UserId when reporting hashtags, cashtags etc.
	 * This function provides a default set of VersionSpaces to allow these to be deserialized.
	 */
	public static VersionSpace[] basicSymphonyVersionSpace() {
		return new VersionSpace[] { 
				new VersionSpace(Taxonomy.class, "1.0"),
				new VersionSpace(Security.class, "1.0", "0.*"),
				new VersionSpace(Mention.class, "1.0"), 
				new VersionSpace(UserId.class, "1.0"), 
				new VersionSpace(Hashtag.class, "1.0"), 
				ObjectMapperFactory.noVersion(Ticker.class), 
				ObjectMapperFactory.noVersion(Cusip.class), 
				ObjectMapperFactory.noVersion(Isin.class), 
				ObjectMapperFactory.noVersion(Openfigi.class),
			};
	}

	/**
	 * Provides all of the classes in the basicSymphonyVersionSpace (above), as well as any you provide in the
	 * varargs.
	 */
	public static VersionSpace[] extendedSymphonyVersionSpace(List<VersionSpace> second) {
		VersionSpace[] cc = new VersionSpace[second.size()];
		return extendedSymphonyVersionSpace(second.toArray(cc));
	}

	/**
	 * Provides all of the classes in the basicSymphonyVersionSpace (above), as well as any you provide in the
	 * varargs.
	 */
	public static VersionSpace[] extendedSymphonyVersionSpace(VersionSpace... first) {
		VersionSpace[] second = basicSymphonyVersionSpace();
		VersionSpace[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
