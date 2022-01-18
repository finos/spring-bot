package com.symphony.api.id;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.symphony.api.id.testing.TestIdentityProvider;

public class JSONTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testLoadAndSavePem() throws IOException {

		Map<String, Object> id = StreamHelp.getProperties("symphony-develop-bot2-identity", Map.class);

		SymphonyIdentity in = new PemSymphonyIdentity((String) id.get("privateKey"),
				new String[] { (String) ((ArrayList<String>) id.get("chain")).get(0) }, (String) id.get("email"));

		SymphonyIdentity out = TestIdentityProvider.getIdentity("symphony-develop-bot2-identity");
		Assertions.assertEquals(in.getPrivateKey(), out.getPrivateKey());
		Assertions.assertEquals(in.getCertificateChain()[0], out.getCertificateChain()[0]);
		Assertions.assertEquals(in.getCommonName(), out.getCommonName());
		Assertions.assertEquals(in.getEmail(), out.getEmail());
}
}
