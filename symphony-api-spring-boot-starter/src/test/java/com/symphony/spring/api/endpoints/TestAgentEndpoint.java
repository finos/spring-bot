package com.symphony.spring.api.endpoints;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.agent.SystemApi;
import com.symphony.spring.api.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("dbtest")
public class TestAgentEndpoint {

	@Autowired
	SystemApi api;

	@Test
	public void testAutowire() throws Exception {
		api.v2HealthCheckGet(null, null);
	}
}