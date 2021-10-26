package org.finos.symphony.toolkit.tools.reminders;

import org.finos.springbot.workflow.form.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JsonConfig implements InitializingBean {

	@Autowired
	EntityJsonConverter ejc;
	
	@Autowired
	FormConverter fc;
		
	@Override
	public void afterPropertiesSet() throws Exception {
		ejc.getObjectMapper()
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
		.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
