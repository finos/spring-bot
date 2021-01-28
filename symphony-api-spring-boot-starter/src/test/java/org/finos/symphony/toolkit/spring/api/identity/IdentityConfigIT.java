package org.finos.symphony.toolkit.spring.api.identity;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.id.SingleSymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;

@ExtendWith(SpringExtension.class)

@SpringBootTest(
		classes={TestApplication.class})
@ActiveProfiles({"develop"})
public class IdentityConfigIT {

	@Autowired
	SymphonyIdentity id;
	
	/**
	 * Because no identity is set, the testing identity should be loaded from settings.xml.
	 * This may or may not work locally, but should definitely work in ci.
	 * @throws Exception
	 */
	@Test
	public void testIdentityLoad() throws Exception {
		Assertions.assertEquals(SingleSymphonyIdentity.class, id.getClass());
		Assertions.assertNotNull(id.getEmail());
		Assertions.assertNotNull(id.getCommonName());
		Assertions.assertNotNull(id.getPrivateKey());
		Assertions.assertNotNull(id.getPublicKey());
		Assertions.assertNull(id.getCertificateChain());
	}
}
