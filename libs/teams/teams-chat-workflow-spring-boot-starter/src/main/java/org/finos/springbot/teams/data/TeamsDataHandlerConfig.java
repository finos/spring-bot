package org.finos.springbot.teams.data;

import java.util.List;

import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.data.AbstractDataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class TeamsDataHandlerConfig extends AbstractDataHandlerConfig {

	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter symphonyEntityJsonConverter() {
		List<VersionSpace> workAnnotatedversionSpaces = scanForWorkClasses();
		ObjectMapper om = new ObjectMapper();
		om = ObjectMapperFactory.initialize(om, workAnnotatedversionSpaces.toArray(new VersionSpace[] {}));		
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.registerModule(new JavaTimeModule());
		return new EntityJsonConverter(om);
	}
}
