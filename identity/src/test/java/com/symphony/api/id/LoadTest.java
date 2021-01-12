package com.symphony.api.id;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoadTest {
	public static final String EMAIL = "thing@thang.com";
	
	P12SymphonyIdentity p12;
	SymphonyIdentity pem; 
	SymphonyIdentity pem2; 
	String publicKey;
	
	@BeforeEach
	public void loadPemAndP12() {
		InputStream is = this.getClass().getResourceAsStream("/example.p12");
		String password = "abc123";
		p12 = new P12SymphonyIdentity(is, password, EMAIL); 

		String privateKey1 = StreamHelp.asString(this.getClass().getResourceAsStream("/privatekey.pem"));
		String privateKey2 =  StreamHelp.asString(this.getClass().getResourceAsStream("/privatekeyrsa.pem"));
		String cert = StreamHelp.asString(this.getClass().getResourceAsStream("/cert.pem"));
		publicKey = StreamHelp.asString(this.getClass().getResourceAsStream("/publickey.pem"));
		pem = new PemSymphonyIdentity(privateKey1, new String[] { cert }, EMAIL);
		pem2 = new PemSymphonyIdentity(privateKey2, new String[] { cert }, EMAIL);
	}

	@Test
	public void p12LoadTest() {
		Assertions.assertEquals("bob", p12.getCommonName());
		Assertions.assertNotNull(p12.getPrivateKey());
		Assertions.assertEquals(1, p12.getCertificateChain().length);
		Assertions.assertEquals(EMAIL, p12.getEmail());
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

