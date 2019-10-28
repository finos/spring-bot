package com.github.deutschebank.symphony.spring.app.jwt;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties={
		"symphony.apis.0.id=9999",
		"symphony.apis.0.pod.url=https://your.agent.domain:443/pod",
		"symphony.app.jwt=true"
	})
public class UserDetailsControllerWithConfiguredPodTest extends AbstractUserDetailsControllerTest {
}
