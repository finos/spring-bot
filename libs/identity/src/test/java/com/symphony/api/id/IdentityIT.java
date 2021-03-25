package com.symphony.api.id;

import javax.net.ssl.X509KeyManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.symphony.api.id.testing.TestIdentityProvider;

public class IdentityIT {

	/**
	 * This will only work if an identity is set as a system property first, 
	 * or available on the file-path.
	 * 
	 * @see {@link TestIdentityProvider}
	 */
	@Test
	public void testIdentityExists() {
		SymphonyIdentity id = TestIdentityProvider.getTestIdentity();
		Assertions.assertNotNull(id);
		Assertions.assertNotNull(id.getCommonName());
		Assertions.assertNotNull(id.getEmail());
		Assertions.assertNotNull(id.getPrivateKey());
		
		id = TestIdentityProvider.getIdentity("symphony-develop-bot2-identity");
		Assertions.assertNotNull(id);
		Assertions.assertNotNull(id.getCommonName());
		Assertions.assertNotNull(id.getEmail());
		Assertions.assertNotNull(id.getPrivateKey());
		
		X509KeyManager km1 = (X509KeyManager) id.getKeyManagers()[0];
		
		Assertions.assertEquals(km1.getPrivateKey("dsfsd"), id.getPrivateKey());
		Assertions.assertEquals(km1.getCertificateChain("dsfsd")[0], id.getCertificateChain()[0]);
		Assertions.assertEquals("ALIAS", km1.getClientAliases(null, null)[0]);
		Assertions.assertEquals("ALIAS", km1.getServerAliases(null, null)[0]);
		
	}
}
