package com.symphony.api.bindings;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;


public class JWTHelperIT {
	
	public static final String NAME = "John Bobbins";
	public static final String EXPECTED_PART1 = "eyJhbGciOiJSUzUxMiJ9";
	public static final String EXPECTED_PART2 = "eyJzdWIiOiJKb2huIEJvYmJpbnMiLCJleHAiOjE1NjIzODk1OTJ9";
	
	@Test
	public void testWithSuppliedId() throws Exception {
		SymphonyIdentity id = TestIdentityProvider.getIdentity("symphony-develop-bot1-identity");
		long future =  1562389592;
		
		String jwt = JWTHelper.createSignedJwt("supercomputa", future, id.getPrivateKey());
		String[] parts = jwt.split("\\.");

		verifySignature(id.getPublicKey(), parts);
		Assertions.assertEquals("{\"alg\":\"RS512\"}{\"sub\":\"supercomputa\",\"exp\":1562389592}", JWTHelper.decodeJwt(jwt));
	}
	
	@Test
	public void testCreateJWT() throws Exception {
		long future =  1562389592;

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		
		keyGen.initialize(4096, new SecureRandom(new byte[] { 1, 2, 4} ));
				
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();
		
		System.out.println("Private Key");
		System.out.println(Base64.getEncoder().encodeToString(priv.getEncoded()));

		System.out.println("Public Key");
		System.out.println(Base64.getEncoder().encodeToString(pub.getEncoded()));
		
		String jwt = JWTHelper.createSignedJwt(NAME, future, priv);

		System.out.println("actual:");
		output(jwt);
		
		// verify the signature using the public key.
		String[] parts = jwt.split("\\.");
		verifySignature(pub, parts);
		
		Assertions.assertEquals(EXPECTED_PART1, parts[0]);
		Assertions.assertEquals(EXPECTED_PART2, parts[1]);
	}

	protected void verifySignature(PublicKey pub, String[] parts)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sigBuilder = Signature.getInstance("SHA512withRSA");
		sigBuilder.initVerify(pub);
		sigBuilder.update((parts[0]+"."+parts[1]).getBytes());
	    byte[] sig = Base64.getUrlDecoder().decode(parts[2]);
	    Assertions.assertTrue(sigBuilder.verify(sig));
	}

	private void outputPart(String p) {
		System.out.println(p);
		System.out.println(new String(Base64.getDecoder().decode(p.getBytes())));
	}
	
	private void output(String in) {
		System.out.println(in);
		String[] parts = in.split("\\.");
		outputPart(parts[0]);
		outputPart(parts[1]);
	}
	
}
