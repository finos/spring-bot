package org.finos.symphony.toolkit.spring.app.jwt;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties={
		"symphony.app.store.location=src/test/resources/pods",
		"symphony.app.proxy.host=myproxy.com",
		"symphony.app.jwt=true"
})
public class UserDetailsControllerWithPodStoreTest extends AbstractUserDetailsControllerTest {

}
