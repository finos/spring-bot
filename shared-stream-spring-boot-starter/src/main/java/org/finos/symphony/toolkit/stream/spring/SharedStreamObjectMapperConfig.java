package org.finos.symphony.toolkit.stream.spring;

import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * If the user doesn't set up an object mapper themselves, this one is created which
 * can map {@link LogMessage}s.
 * 
 * @author rob@kite9.com
 *
 */
@Configuration
public class SharedStreamObjectMapperConfig {

	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper fallbackObjectMapper() {
		ObjectMapper out = new ObjectMapper();
		ObjectMapperFactory.initialize(out, LogMessage.VERSION_SPACE);
		return out;
	}
}
