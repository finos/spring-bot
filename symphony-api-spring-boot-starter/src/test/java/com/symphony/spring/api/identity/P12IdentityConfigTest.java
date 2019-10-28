package com.symphony.spring.api.identity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.id.P12SymphonyIdentity;
import com.symphony.id.SymphonyIdentity;
import com.symphony.spring.api.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes={TestApplication.class})
@ActiveProfiles("p12idtest")
public class P12IdentityConfigTest {

	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityLoad() throws Exception {
		Assert.assertEquals(P12SymphonyIdentity.class, id.getClass());
		Assert.assertEquals("robski.moff@example.com", id.getEmail());
		Assert.assertEquals("bob", id.getCommonName());
		Assert.assertNotNull(id.getPrivateKey());
		Assert.assertNotNull(id.getPublicKey());
		Assert.assertEquals(1, id.getCertificateChain().length);
	}
}
