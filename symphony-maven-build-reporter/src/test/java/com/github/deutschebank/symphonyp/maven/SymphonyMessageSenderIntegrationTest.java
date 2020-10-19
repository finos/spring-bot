package com.github.deutschebank.symphonyp.maven;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Developer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;

public class SymphonyMessageSenderIntegrationTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SymphonyMessageSenderIntegrationTest.class);
	public static final String AGENT_URL = "https://develop.symphony.com/agent";
	public static final String POD_URL = "https://develop.symphony.com/pod";
	public static final String LOGIN_URL = "https://develop.symphony.com/login";
	public static final String RELAY_URL = "https://develop.symphony.com/relay";

	@Test
	public void testSymphonyMessageSender() throws IOException {
		SymphonyIdentity id = TestIdentityProvider.getTestIdentity();
		ProxyingWrapper pod = new ProxyingWrapper(null, Collections.singletonList(null), POD_URL, id, LOGGER);
		ProxyingWrapper agent = new ProxyingWrapper(null, Collections.singletonList(null), AGENT_URL, id, LOGGER);
		ProxyingWrapper relay = new ProxyingWrapper(null, Collections.singletonList(null), RELAY_URL, id, LOGGER);
		ProxyingWrapper login = new ProxyingWrapper(null, Collections.singletonList(null), LOGIN_URL, id, LOGGER);

		SymphonyMessageSender sms = new SymphonyMessageSender(pod, agent, null, null, relay, login, id);
		Map<String, Object> data = new HashMap<String, Object>();
		
		Developer d = new Developer();
		d.setName("John Johnson");
		d.setEmail("john@example.com");

		data.put("projects", Collections.emptyList());
		data.put("exceptions", Collections.emptyList());
		data.put("developers", Collections.singletonList(d));
		data.put("title", "Some Test Project");
		data.put("date", new Date());
		data.put("passed", true);
		data.put("url", "https://github.com/deutschebank/symphony-java-toolkit");
		data.put("recipients", Collections.singletonList("y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA=="));
		data.put("hashtags", Collections.singletonList("some-hash-tag"));
		data.put("version", "1.0");
		data.put("type", "com.github.deutschebank.symphony.maven-event");

		sms.sendMessage(Collections.singletonMap("event", data));
	}

}
