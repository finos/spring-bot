package org.finos.symphony.toolkit.spring.api.endpoints;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.ListNamesResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.agent.SystemApi;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


@ExtendWith(SpringExtension.class)

@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("develop")
@TestPropertySource(properties = {"management.endpoints.web.exposure.include=*"})
public class HealthEndpointIT {

	@Autowired
	SystemApi api;

	@Autowired
	HealthEndpoint he;
	
	@Autowired
	MetricsEndpoint me;
	
	@Autowired
	MeterRegistry mr;
	
	@Test
	public void testAutowire() {
		System.out.println(api.v3ExtendedHealth());
		
		HealthComponent h = he.health();
		Assertions.assertEquals(Status.UP, h.getStatus());
		Assertions.assertNotNull(he.healthForPath("symphony-api-symphony.practice.bot1-develop"));
		
		ListNamesResponse lnr = me.listNames();
		String symphonyName = lnr.getNames().stream().filter(n -> n.startsWith("symphony")).findFirst().orElseThrow(() -> new RuntimeException("Couldn't find timer"));
		Assertions.assertEquals("symphony.api-call", symphonyName);
		Collection<Timer> timers = mr.get(symphonyName).timers();
		Assertions.assertTrue(timers.size() ==1);
		Timer agentCallTImer = timers.stream().filter(t -> t.getId().toString().contains("agent")).findFirst().orElse(null);
		Assertions.assertEquals("symphony.practice.bot1", agentCallTImer.getId().getTag("id"));
		Assertions.assertEquals("v3ExtendedHealth", agentCallTImer.getId().getTag("method"));
		Assertions.assertEquals("develop", agentCallTImer.getId().getTag("pod"));
		Assertions.assertTrue(agentCallTImer.totalTime(TimeUnit.NANOSECONDS) > 1);
	}
}