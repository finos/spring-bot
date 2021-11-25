package org.finos.springbot.symphony.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.data.AbstractDataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.Taxonomy;
import org.symphonyoss.fin.Security;
import org.symphonyoss.fin.security.id.Cusip;
import org.symphonyoss.fin.security.id.Isin;
import org.symphonyoss.fin.security.id.Openfigi;
import org.symphonyoss.fin.security.id.Ticker;
import org.symphonyoss.taxonomy.Hashtag;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
import com.symphony.user.Mention;
import com.symphony.user.StreamID;
import com.symphony.user.UserId;

@Configuration
public class SymphonyDataHandlerCofig extends AbstractDataHandlerConfig {

	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter symphonyEntityJsonConverter() {
		List<VersionSpace> workAnnotatedversionSpaces = scanForWorkClasses();
		
		List<VersionSpace> chatWorkflowVersionSpaces = Arrays.asList(			
			new VersionSpace(UserId.class, "1.0"), 
			new VersionSpace(DisplayName.class, "1.0"), 
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
		
		ObjectMapper om = new ObjectMapper();
		
		om = ObjectMapperFactory.initialize(om, extendedSymphonyVersionSpace(combined));		
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.registerModule(new JavaTimeModule());
		om.registerModule(new LegacyFormatModule());
				
		return new EntityJsonConverter(om);
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
