package org.finos.symphony.toolkit.spring.api.endpoints;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

/**
 * This is designed to run on travis, and so will probably fail if behind a (real) proxy.
 * 
 * @author Rob Moffat
 *
 */
@ExtendWith(SpringExtension.class)

@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles({"proxies", "crt"})
public class ProxiedEndpointIT {

	@Autowired
	UsersApi api;
	
	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testAutowire() throws Exception {
		// enhance this with custom trust store. (maybe even have test with invalid trust store)
		System.out.println(api.v3UsersGet(null, null, null, id.getCommonName(), true, true));
	}
}