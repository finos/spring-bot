package org.finos.springbot.symphony.stream;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.entities.VersionSpaceHelp;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@Import(ChatWorkflowConfig.class)
public class TestApplication implements InitializingBean {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
	
	@Autowired
	EntityJsonConverter ejc;

	@Override
	public void afterPropertiesSet() throws Exception {
		VersionSpaceHelp.basicSymphonyVersionSpace()
			.stream().forEach(vs -> ejc.addVersionSpace(vs));
	}
}
