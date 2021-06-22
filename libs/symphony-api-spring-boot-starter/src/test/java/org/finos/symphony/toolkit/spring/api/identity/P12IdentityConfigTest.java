package org.finos.symphony.toolkit.spring.api.identity;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.id.P12SymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;

@ExtendWith(SpringExtension.class)

@SpringBootTest(
		classes={TestApplication.class})
@ActiveProfiles({"p12idtest", "develop"})
public class P12IdentityConfigTest {

	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityLoad() throws Exception {
		Assertions.assertEquals(P12SymphonyIdentity.class, id.getClass());
		Assertions.assertEquals("robski.moff@example.com", id.getEmail());
		Assertions.assertEquals("bob", id.getCommonName());
		Assertions.assertNotNull(id.getPrivateKey());
		Assertions.assertNotNull(id.getPublicKey());
		Assertions.assertEquals(1, id.getCertificateChain().length);
	}
}
