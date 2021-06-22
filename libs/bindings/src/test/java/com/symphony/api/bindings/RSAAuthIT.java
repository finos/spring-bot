package com.symphony.api.bindings;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.login.AuthenticationApi;
import com.symphony.api.model.AuthenticateRequest;
import com.symphony.api.model.Token;

public class RSAAuthIT extends AbstractIT{

	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testSessionAuthWithRSA(TestClientStrategy s) throws Exception {
		SymphonyIdentity id = s.getIdentity();
		String jwt = JWTHelper.createSignedJwt(id.getCommonName(), id.getPrivateKey());
		System.out.println(jwt);
		System.out.println(JWTHelper.decodeJwt(jwt));
		AuthenticationApi sessionApi = s.getRSASessionAuthApi();
		
		AuthenticateRequest req = new AuthenticateRequest();
		req.setToken(jwt);
		
		Token done = sessionApi.pubkeyAuthenticatePost(req);
		System.out.println(done);
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testKeyManagerAuthWithRSA(TestClientStrategy s) throws Exception {
		SymphonyIdentity id = s.getIdentity();
		String jwt = JWTHelper.createSignedJwt(id.getCommonName(), id.getPrivateKey());
	
		AuthenticationApi keyApi = s.getRSAKeyAuthApi();
		
		AuthenticateRequest req = new AuthenticateRequest();
		req.setToken(jwt);
		
		Token done = keyApi.pubkeyAuthenticatePost(req);
		System.out.println(done);
	}
}
