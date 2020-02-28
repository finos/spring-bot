package com.github.deutschebank.symphony.spring.api.endpoints;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.spring.api.TestApplication;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

/**
 * This is designed to run on travis, and so will probably fail if behind a (real) proxy.
 * 
 * @author Rob Moffat
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles({"proxies", "crt"})
public class TestProxiedEndpoint {

	@Autowired
	UsersApi api;
	
	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testAutowire() throws Exception {
		// enhance this with custom trust store. (maybe even have test with invalid trust store)
		System.out.println(api.v3UsersGet(null, null, null, id.getCommonName(), true));
	}
}