package com.db.symphony;


import javax.net.ssl.KeyManager;

import com.db.symphony.id.SymphonyIdentity;
import com.db.symphony.id.testing.TestIdentityProvider;
import com.symphony.api.JWTHelper;
import com.symphony.api.TokenManager;
import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.cxf.CXFApiBuilder;
import com.symphony.api.jersey.JerseyApiBuilder;
import com.symphony.api.model.AuthenticateRequest;

/**
 * Returns apis for use in the test cases.  In order to make this work (for DB internally only)
 * system properties for symphony.certificate.password and symphony.certificate.file (pointing to a p12 file) 
 * must be supplied
 * 
 * @author Rob Moffat
 *
 */
public class DBPodConfig {
	
	public static final SymphonyIdentity BOT2_ID = TestIdentityProvider.getIdentity("symphony-develop-bot2-identity");
	public static final SymphonyIdentity BOT1_ID = TestIdentityProvider.getIdentity("symphony-develop-bot1-identity");
	
	public static final String CI_PROXY = System.getProperty("proxy");
	public static final String SESSION_AUTH_URL = "https://develop-api.symphony.com/sessionauth";
	public static final String KEY_AUTH_URL = "https://develop-api.symphony.com/keyauth";
	public static final String AGENT_URL = "https://develop.symphony.com/agent";
	public static final String POD_URL = "https://develop.symphony.com/pod";
	public static final String LOGIN_URL = "https://develop.symphony.com/login";
	public static final String RELAY_URL = "https://develop.symphony.com/relay";
	
	public static KeyManager[] getKeyManagersBot1() {
		return BOT1_ID.getKeyManagers();
	}
		
	public static KeyManager[] getKeyManagersBot2() {
		return BOT2_ID.getKeyManagers();
	}

	
	public static final TestClientStrategy JERSEY_RSA = new TestClientStrategy() {
		
		private TokenManager tm;
		
		{
			com.symphony.api.login.AuthenticationApi sessionAuthApi = getRSASessionAuthApi();
			com.symphony.api.login.AuthenticationApi keyAuthApi = getRSAKeyAuthApi();
			tm = new TokenManager(
					() -> sessionAuthApi.pubkeyAuthenticatePost(
							new AuthenticateRequest().token(createToken())), 
					
					() -> keyAuthApi.pubkeyAuthenticatePost(
							new AuthenticateRequest().token(createToken()))); 
		}

		private String createToken() {
			try {
				return JWTHelper.createSignedJwt(getIdentity().getCommonName(), getIdentity().getPrivateKey());
			} catch (Exception e) {
				throw new IllegalArgumentException("Couldn't create token", e);
			}
		}
		
		@Override
		public AuthenticationApi getSessionAuthApi() {
			throw new UnsupportedOperationException("Bot 1 only allows RSA Login");
		}
		
		@Override
		public <X> X getPodApi(Class<X> api) throws Exception {
			JerseyApiBuilder b = new JerseyApiBuilder(POD_URL);
			b.setTokenManager(tm);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			return b.getApi(api);
		}
		
		@Override
		public AuthenticationApi getKeyAuthApi() {
			throw new UnsupportedOperationException("Bot 1 only allows RSA Login");
		}
		
		@Override
		public SymphonyIdentity getIdentity() {
			return BOT1_ID;
		}
		
		@Override
		public <X> X getAgentApi(Class<X> api) throws Exception {
			JerseyApiBuilder b = new JerseyApiBuilder(AGENT_URL);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			b.setTokenManager(tm);
			return b.getApi(api);

		}

		@Override
		public TokenManager getTokenManager() {
			return tm;
		}

		@Override
		public com.symphony.api.login.AuthenticationApi getRSASessionAuthApi() {
			JerseyApiBuilder b = new JerseyApiBuilder(LOGIN_URL, getKeyManagersBot1());
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			return b.getApi(com.symphony.api.login.AuthenticationApi.class);
		}

		@Override
		public com.symphony.api.login.AuthenticationApi getRSAKeyAuthApi() {
			JerseyApiBuilder b = new JerseyApiBuilder(RELAY_URL, getKeyManagersBot1());
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			return b.getApi(com.symphony.api.login.AuthenticationApi.class);
		}
	};
	
	public static final TestClientStrategy CXF_CERT = new TestClientStrategy() {
		
		private TokenManager tm;
		
		{
			AuthenticationApi sessionAuthApi = getSessionAuthApi();
			AuthenticationApi keyAuthApi = getKeyAuthApi();
			tm = new TokenManager(() -> sessionAuthApi.v1AuthenticatePost(), () -> keyAuthApi.v1AuthenticatePost()); 
		}
		
		@Override
		public AuthenticationApi getSessionAuthApi() {
			CXFApiBuilder b = new CXFApiBuilder(SESSION_AUTH_URL, getKeyManagersBot2());
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			return b.getApi(AuthenticationApi.class);
		}
		
		@Override
		public <X> X getPodApi(Class<X> api) throws Exception {
			CXFApiBuilder b = new CXFApiBuilder(POD_URL);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			b.setTokenManager(tm);
			return b.getApi(api);
		}
		
		@Override
		public AuthenticationApi getKeyAuthApi() {
			CXFApiBuilder b = new CXFApiBuilder(KEY_AUTH_URL, getKeyManagersBot2());
			return b.getApi(AuthenticationApi.class);
		}
		
		@Override
		public SymphonyIdentity getIdentity() {
			return BOT2_ID;
		}
		
		@Override
		public <X> X getAgentApi(Class<X> api) throws Exception {
			CXFApiBuilder b = new CXFApiBuilder(AGENT_URL);
			b.setTokenManager(tm);
			return b.getApi(api);
		}

		@Override
		public TokenManager getTokenManager() {
			return tm;
		}

		@Override
		public com.symphony.api.login.AuthenticationApi getRSASessionAuthApi() {
			throw new UnsupportedOperationException("Bot 2 only allows cert Login");
		}
		
		@Override
		public com.symphony.api.login.AuthenticationApi getRSAKeyAuthApi() {
			throw new UnsupportedOperationException("Bot 2 only allows cert Login");
		}

	};
}
