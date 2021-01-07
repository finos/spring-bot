package org.finos.symphony.toolkit.spring.api.endpoints;

import org.finos.symphony.toolkit.spring.api.SymphonyApiAutowireConfig;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.builders.CXFApiBuilderConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.agent.SystemApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={SymphonyApiConfig.class, SymphonyApiAutowireConfig.class, CXFApiBuilderConfig.class, ObjectMapperConfig.class})
@ActiveProfiles({"develop", "crt"})
public class AgentEndpointCXFIT {

	@Autowired
	SystemApi api;
	
	@Autowired
	MessagesApi messages;
	
	@Value("${test-room}")
	String streamId;

	@Test
	public void testAutowire() throws Exception {
		api.v2HealthCheckGet(false, false, false, false, false, false, false, false, null, null);
	}
	
	@Test
	public void testSendMessage() throws Exception {
		messages.v4StreamSidMessageCreatePost(null, streamId, "<messageML>test</messageML>", null, null, null, null, null);
	}
}