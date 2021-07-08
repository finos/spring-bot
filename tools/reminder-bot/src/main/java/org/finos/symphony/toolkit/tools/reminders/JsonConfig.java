package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JsonConfig implements InitializingBean {

	@Autowired
	EntityJsonConverter ejc;
	
	@Autowired
	FormConverter fc;
		
	@Override
	public void afterPropertiesSet() throws Exception {
		ejc.getObjectMapper().registerModule(new JavaTimeModule());
		ejc.getObjectMapper()
    	.enable(SerializationFeature.INDENT_OUTPUT)
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
    	.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		
		fc.getObjectMapper().registerModule(new JavaTimeModule());
	}
}
