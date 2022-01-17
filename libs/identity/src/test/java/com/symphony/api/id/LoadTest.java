package com.symphony.api.id;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.json.SymphonyIdentityModule;

public class LoadTest {
	public static final String EMAIL = "thing@thang.com";

	P12SymphonyIdentity p12;
	SymphonyIdentity pem;
	SymphonyIdentity pem2;
	String publicKey;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void loadPemAndP12() {
		InputStream is = this.getClass().getResourceAsStream("/example.p12");

		Map<String, Object> bot3Id = StreamHelp.getProperties("symphony-develop-bot3-identity", Map.class);
		Map<String, Object> privateKeyRsa = StreamHelp.getProperties("symphony-test-identity", Map.class);
		String p12Pass = (String) bot3Id.get("password");
		p12 = new P12SymphonyIdentity(is, p12Pass, EMAIL);
		
		String cert = (String) ((ArrayList<String>) bot3Id.get("chain")).get(0);
		publicKey = (String) bot3Id.get("publicKey");
		pem = new PemSymphonyIdentity((String) bot3Id.get("privateKey"), new String[] { cert }, EMAIL);
		pem2 = new PemSymphonyIdentity((String) privateKeyRsa.get("privateKey"), new String[] { cert }, EMAIL);
	}

	@Test
	public void p12LoadTest() throws JsonProcessingException, Exception {
		Assertions.assertEquals("bob", p12.getCommonName());
		Assertions.assertNotNull(p12.getPrivateKey());
		Assertions.assertEquals(1, p12.getCertificateChain().length);
		Assertions.assertEquals(EMAIL, p12.getEmail());
		
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new SymphonyIdentityModule());
		// check deserializing public keys
		String serd = om.writeValueAsString(p12.getPublicKey());
		PublicKey back = om.readValue(serd, PublicKey.class);
		Assertions.assertEquals(p12.getPublicKey(), back);
	}

	@Test
	public void testPemLoad() {
		Assertions.assertEquals("bob", pem.getCommonName());
		Assertions.assertNotNull(pem.getPrivateKey());
		Assertions.assertEquals(1, pem.getCertificateChain().length);
		Assertions.assertEquals(EMAIL, pem.getEmail());
	}

	@Test
	public void testSame() {
		Assertions.assertEquals(pem.getPrivateKey(), p12.getPrivateKey());
		Assertions.assertEquals(pem.getCertificateChain()[0], p12.getCertificateChain()[0]);
	}

	@Test
	public void testPublicKeyLoad() {
		Assertions.assertNotNull(PemSymphonyIdentity.createPublicKeyFromString(publicKey));
	}

	@Test
	public void testPRivateKeyRsa() throws Exception {
		Assertions.assertNotNull(pem2.getPrivateKey());
		Assertions.assertNotNull(pem2.getPublicKey());

	}
}
