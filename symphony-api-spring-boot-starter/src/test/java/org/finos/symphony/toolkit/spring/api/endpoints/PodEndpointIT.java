package org.finos.symphony.toolkit.spring.api.endpoints;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

/**
 * NOTE: This probably won't work on the local PC due to proxies.  You can override the property to use userproxy if you want.
 * 
 * @author Rob Moffat
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("develop")
public class PodEndpointIT {

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