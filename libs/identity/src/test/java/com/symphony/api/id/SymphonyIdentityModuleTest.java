package com.symphony.api.id;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.json.SymphonyIdentityModule;

public class SymphonyIdentityModuleTest {
	
	@Test
	public void testModule() throws Exception {
		
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new SymphonyIdentityModule());
		InputStream is = this.getClass().getResourceAsStream("/example.p12");
		String password = "abc123";
		P12SymphonyIdentity p12 = new P12SymphonyIdentity(is, password, ""); 
		
		Map<String, Object> stuff = new LinkedHashMap<>();
		stuff.put("certs", p12.getCertificateChain());
		stuff.put("key", p12.getPrivateKey());
		stuff.put("pub", p12.getPublicKey());
		
		String written = om.writeValueAsString(stuff);
		//System.out.println(written);
		
		String expected = StreamHelp.asString(this.getClass().getResourceAsStream("/map.json"));
		Assertions.assertEquals(expected, written);
		
		// test individually
		String certs = om.writeValueAsString(p12.getCertificateChain());
		X509Certificate[] reloaded = om.readValue(certs, X509Certificate[].class);
		String pk = om.writeValueAsString(p12.getPrivateKey());
		PrivateKey pkReloaded = om.readValue(pk, PrivateKey.class);
		
		Assertions.assertEquals(p12.getPrivateKey(), pkReloaded);
		Assertions.assertEquals(p12.getCertificateChain()[0], reloaded[0]);
		
		// check deserializing public keys
		String serd = om.writeValueAsString(p12.getPublicKey());
		PublicKey back = om.readValue(serd, PublicKey.class);
		Assertions.assertEquals(p12.getPublicKey(), back);
		
	}
}
