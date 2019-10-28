package com.symphony.spring.api.endpoints;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.pod.UsersApi;
import com.symphony.id.SymphonyIdentity;
import com.symphony.spring.api.TestApplication;

/**
 * NOTE: This probably won't work on the local PC due to proxies.  You can override the property to use userproxy if you want.
 * 
 * @author Rob Moffat
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("dbtest")
public class TestPodEndpoint {

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