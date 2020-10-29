package org.finos.symphony.toolkit.spring.api.endpoints;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.agent.SystemApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles({"develop", "crt"})
public class TestAgentEndpoint {

	@Autowired
	SystemApi api;

	@Test
	public void testAutowire() throws Exception {
		api.v2HealthCheckGet(false, false, false, false, false, false, false, false, null, null);
	}
}