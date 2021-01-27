package org.finos.symphony.toolkit.spring.api.endpoints;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.agent.SystemApi;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles({"proxy", "crt"})
public class ProxyLoadingIT {

	@Autowired
	SymphonyApiProperties properties;
	
	@Autowired
	SystemApi api;

	@Test
	public void testAutowire() throws Exception {
		api.v2HealthCheckGet(false, false, false, false, false, false, false, false, null, null);
	}
	
	@Test
	public void testThatProxiesAreThere() {
		Assertions.assertEquals("blah", properties.getApis().get(0).getPod().getProxies().get(0).getHost());
		Assertions.assertEquals(3, properties.getApis().get(0).getPod().getProxies().size());
		Assertions.assertEquals("single", properties.getApis().get(0).getSessionAuth().getProxy().getHost());
	}
}
