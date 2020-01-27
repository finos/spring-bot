package com.github.deutschebank.symphony.spring.api.endpoints;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.spring.api.TestApplication;
import com.symphony.api.agent.SystemApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles({"develop", "crt"})
public class TestAgentEndpoint {

	@Autowired
	SystemApi api;

	@Test
	public void testAutowire() throws Exception {
		api.v2HealthCheckGet(false, null, null);
	}
}