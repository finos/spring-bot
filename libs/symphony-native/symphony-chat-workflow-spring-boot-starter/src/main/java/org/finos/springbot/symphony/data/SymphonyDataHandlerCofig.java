package org.finos.springbot.symphony.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.finos.springbot.entities.VersionSpaceHelp;
import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.data.AbstractDataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.fin.security.id.Cusip;
import org.symphonyoss.fin.security.id.Isin;
import org.symphonyoss.fin.security.id.Openfigi;
import org.symphonyoss.fin.security.id.Ticker;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
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
			RoomWelcomeEventConsumer.VERSION_SPACE);
		
		List<VersionSpace> combined = new ArrayList<>();
		combined.addAll(chatWorkflowVersionSpaces);
		combined.addAll(workAnnotatedversionSpaces);
		
		ObjectMapper om = new ObjectMapper();
		
		om = ObjectMapperFactory.initialize(om, VersionSpaceHelp.extendedSymphonyVersionSpace(combined));		
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.registerModule(new JavaTimeModule());
		om.registerModule(new LegacyFormatModule());
				
		return new EntityJsonConverter(om);
	}
}
