package com.github.deutschebank.symphony.spring.api.endpoints;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.ListNamesResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.spring.api.TestApplication;
import com.symphony.api.agent.SystemApi;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("develop")
@TestPropertySource(properties = {"management.endpoints.web.exposure.include=*"})
public class TestHealthEndpoint {

	@Autowired
	SystemApi api;

	@Autowired
	HealthEndpoint he;
	
	@Autowired
	MetricsEndpoint me;
	
	@Autowired
	MeterRegistry mr;
	
	@Test
	public void testAutowire() throws Exception {
		System.out.println(api.v2HealthCheckGet(false,false,false,false,false,false,false,false, null, null));
		
		HealthComponent h = he.health();
		Assert.assertEquals(Status.UP, h.getStatus());
		Assert.assertNotNull(he.healthForPath("symphony-api-symphony.practice.bot1-develop"));
		
		ListNamesResponse lnr = me.listNames();
		String symphonyName = lnr.getNames().stream().filter(n -> n.startsWith("symphony")).findFirst().orElseThrow(() -> new RuntimeException("Couldn't find timer"));
		Assert.assertEquals("symphony.api-call", symphonyName);
		Collection<Timer> timers = mr.get(symphonyName).timers();
		Assert.assertTrue(timers.size() >=3);
		Timer agentCallTImer = timers.stream().filter(t -> t.getId().toString().contains("agent")).findFirst().orElse(null);
		Assert.assertEquals("symphony.practice.bot1", agentCallTImer.getId().getTag("id"));
		Assert.assertEquals("v2HealthCheckGet", agentCallTImer.getId().getTag("method"));
		Assert.assertEquals("develop", agentCallTImer.getId().getTag("pod"));
		Assert.assertTrue(agentCallTImer.totalTime(TimeUnit.NANOSECONDS) > 1);
	}
}