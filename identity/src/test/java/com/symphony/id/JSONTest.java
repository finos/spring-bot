package com.symphony.id;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.id.PemSymphonyIdentity;
import com.symphony.id.SymphonyIdentity;
import com.symphony.id.json.SymphonyIdentityModule;

public class JSONTest {

	@Test
	public void testLoadAndSavePem() throws IOException {
		
		String privateKey = StreamHelp.asString(this.getClass().getResourceAsStream("/privatekey.pem"));
		String cert = StreamHelp.asString(this.getClass().getResourceAsStream("/cert.pem"));
		SymphonyIdentity in = new PemSymphonyIdentity(privateKey, new String[] { cert }, "rob@example.com");
		
		ObjectMapper om = new ObjectMapper();
		
		String json1 = om.writeValueAsString(in);
		String expected = StreamHelp.asString(this.getClass().getResourceAsStream("/pemId.json"));
		Assert.assertEquals(expected, json1);
	
		SymphonyIdentity out = om.readValue(json1, SymphonyIdentity.class);
		Assert.assertEquals(in.getPrivateKey(), out.getPrivateKey());
		Assert.assertEquals(in.getCertificateChain()[0], out.getCertificateChain()[0]);
		Assert.assertEquals(in.getCommonName(), out.getCommonName());
		Assert.assertEquals(in.getEmail(), out.getEmail());
	}
}
