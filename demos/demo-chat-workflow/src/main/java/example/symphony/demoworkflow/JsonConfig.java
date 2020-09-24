package example.symphony.demoworkflow;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;

@Configuration
public class JsonConfig implements InitializingBean {

	@Autowired
	EntityJsonConverter ejc;
		
	@Override
	public void afterPropertiesSet() throws Exception {
		ejc.getObjectMapper().registerModule(new JavaTimeModule());
	}

}
