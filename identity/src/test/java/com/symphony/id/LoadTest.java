package com.symphony.id;

import java.io.InputStream;
import java.security.PublicKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.symphony.id.P12SymphonyIdentity;
import com.symphony.id.PemSymphonyIdentity;
import com.symphony.id.SymphonyIdentity;

public class LoadTest {
	public static final String EMAIL = "thing@thang.com";
	
	P12SymphonyIdentity p12;
	SymphonyIdentity pem; 
	SymphonyIdentity pem2; 
	String publicKey;
	
	@Before
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
		Assert.assertEquals("bob", p12.getCommonName());
		Assert.assertNotNull(p12.getPrivateKey());
		Assert.assertEquals(1, p12.getCertificateChain().length);
		Assert.assertEquals(EMAIL, p12.getEmail());
	}
	
	@Test
	public void testPemLoad() {
		Assert.assertEquals("bob", pem.getCommonName());
		Assert.assertNotNull(pem.getPrivateKey());
		Assert.assertEquals(1, pem.getCertificateChain().length);
		Assert.assertEquals(EMAIL, pem.getEmail());
	}
	
	@Test
	public void testSame() {
		Assert.assertEquals(pem.getPrivateKey(), p12.getPrivateKey());
		Assert.assertEquals(pem.getCertificateChain()[0], p12.getCertificateChain()[0]);
	}
	
	@Test
	public void testPublicKeyLoad() {
		Assert.assertNotNull(PemSymphonyIdentity.createPublicKeyFromString(publicKey));
	}
	
	@Test
	public void testPRivateKeyRsa() throws Exception {
		Assert.assertNotNull(pem2.getPrivateKey());
		Assert.assertNotNull(pem2.getPublicKey());
		
	}
}

