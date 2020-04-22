package com.github.deutschebank.symphony.spring.api.endpoints;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.spring.api.TestApplication;
import com.github.deutschebank.symphony.spring.api.properties.SymphonyApiProperties;
import com.symphony.api.agent.SystemApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles({"proxy", "crt"})
public class TestProxyLoading {

	@Autowired
	SymphonyApiProperties properties;
	
	@Autowired
	SystemApi api;

	@Test
	public void testAutowire() throws Exception {
		api.v2HealthCheckGet(false, null, null);
	}
	
	@Test
	public void testThatProxiesAreThere() {
		Assert.assertEquals("blah", properties.getApis().get(0).getPod().getProxies().get(0).getHost());
		Assert.assertEquals(3, properties.getApis().get(0).getPod().getProxies().size());
		Assert.assertEquals("single", properties.getApis().get(0).getSessionAuth().getProxy().getHost());
	}
}
