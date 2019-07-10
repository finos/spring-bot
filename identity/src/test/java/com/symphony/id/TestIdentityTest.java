package com.symphony.id;

import javax.net.ssl.X509KeyManager;

import org.junit.Assert;
import org.junit.Test;

import com.symphony.id.SymphonyIdentity;
import com.symphony.id.testing.TestIdentityProvider;

public class TestIdentityTest {

	/**
	 * This will only work if an identity is set as a system property first, 
	 * or available on the file-path.
	 * 
	 * @see {@link TestIdentityProvider}
	 */
	@Test
	public void testIdentityExists() {
		SymphonyIdentity id = TestIdentityProvider.getTestIdentity();
		Assert.assertNotNull(id);
		Assert.assertNotNull(id.getCommonName());
		Assert.assertNotNull(id.getEmail());
		Assert.assertNotNull(id.getPrivateKey());
		
		X509KeyManager km1 = (X509KeyManager) id.getKeyManagers()[0];
		
		Assert.assertEquals(km1.getPrivateKey("dsfsd"), id.getPrivateKey());
		Assert.assertEquals(km1.getCertificateChain("dsfsd")[0], id.getCertificateChain()[0]);
		Assert.assertEquals("ALIAS", km1.getClientAliases(null, null)[0]);
		Assert.assertEquals("ALIAS", km1.getServerAliases(null, null)[0]);
		
	}
}
