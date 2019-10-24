package com.symphony.spring.api.identity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.id.SingleSymphonyIdentity;
import com.symphony.id.SymphonyIdentity;
import com.symphony.spring.api.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes={TestApplication.class})
public class TestingIdentityConfigTest {

	@Autowired
	SymphonyIdentity id;
	
	/**
	 * Because no identity is set, the testing identity should be loaded from settings.xml.
	 * This may or may not work locally, but should definitely work in ci.
	 * @throws Exception
	 */
	@Test
	public void testIdentityLoad() throws Exception {
		Assert.assertEquals(SingleSymphonyIdentity.class, id.getClass());
		Assert.assertNotNull(id.getEmail());
		Assert.assertNotNull(id.getCommonName());
		Assert.assertNotNull(id.getPrivateKey());
		Assert.assertNotNull(id.getPublicKey());
		Assert.assertTrue(id.getCertificateChain().length >= 1);
	}
}
