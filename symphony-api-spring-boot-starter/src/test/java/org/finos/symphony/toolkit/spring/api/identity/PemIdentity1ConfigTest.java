package org.finos.symphony.toolkit.spring.api.identity;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.id.PemSymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes={TestApplication.class})
@ActiveProfiles("pemidtest1")
public class PemIdentity1ConfigTest {

	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityLoad() throws Exception {
		Assert.assertEquals(PemSymphonyIdentity.class, id.getClass());
		Assert.assertEquals("robski.moff@example.com", id.getEmail());
		Assert.assertEquals("bob", id.getCommonName());
		Assert.assertNotNull(id.getPrivateKey());
		Assert.assertNotNull(id.getPublicKey());
		Assert.assertEquals(null, id.getCertificateChain());
	}
}
