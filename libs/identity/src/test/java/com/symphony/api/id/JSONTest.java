package com.symphony.api.id;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONTest {

	@Test
	public void testLoadAndSavePem() throws IOException {
		
		String privateKey = StreamHelp.asString(this.getClass().getResourceAsStream("/privatekey.pem"));
		String cert = StreamHelp.asString(this.getClass().getResourceAsStream("/cert.pem"));
		SymphonyIdentity in = new PemSymphonyIdentity(privateKey, new String[] { cert }, "rob@example.com");
		
		ObjectMapper om = new ObjectMapper();
		
		String json1 = om.writeValueAsString(in);
		String expected = StreamHelp.asString(this.getClass().getResourceAsStream("/pemId.json"));
		Assertions.assertEquals(expected, json1);
	
		SymphonyIdentity out = om.readValue(json1, SymphonyIdentity.class);
		Assertions.assertEquals(in.getPrivateKey(), out.getPrivateKey());
		Assertions.assertEquals(in.getCertificateChain()[0], out.getCertificateChain()[0]);
		Assertions.assertEquals(in.getCommonName(), out.getCommonName());
		Assertions.assertEquals(in.getEmail(), out.getEmail());
	}
}
