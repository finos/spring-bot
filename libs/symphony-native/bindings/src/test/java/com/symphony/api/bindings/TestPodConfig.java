package com.symphony.api.bindings;


import org.glassfish.jersey.internal.util.Producer;

import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.cxf.CXFApiBuilder;
import com.symphony.api.bindings.jersey.JerseyApiBuilder;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;
import com.symphony.api.login.AuthenticationApi;
import com.symphony.api.model.AuthenticateRequest;

/**
 * Returns apis for use in the test cases.  In order to make this work
 * system properties for symphony.certificate.password and symphony.certificate.file (pointing to a p12 file) 
 * must be supplied
 * 
 * @author Rob Moffat
 *
 */
public class TestPodConfig {
	
	public static abstract class AbstractTestClientStrategy implements TestClientStrategy {
		
		SymphonyIdentity id;
		Producer<ConfigurableApiBuilder> pab;
		TokenManager tm;
		StreamIDHelp streamHelp;
		
		public AbstractTestClientStrategy(SymphonyIdentity id, Producer<ConfigurableApiBuilder> pab) {
			this.id = id;
			this.pab = pab;
			this.tm = initializeTokenManager();
			this.streamHelp = new StreamIDHelp();
		}
		
		protected abstract TokenManager initializeTokenManager();

		@Override
		public CertificateAuthenticationApi getSessionAuthApi() {
			ConfigurableApiBuilder b = pab.call(); 
			b.setUrl(SESSION_AUTH_URL);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			b.setKeyManagers(id.getKeyManagers());
			return b.getApi(CertificateAuthenticationApi.class);
		}

		@Override
		public <X> X getPodApi(Class<X> api) throws Exception {
			ConfigurableApiBuilder b = pab.call();
			b.setUrl(POD_URL);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			b.setWrappers(new ApiWrapper[] { tm, streamHelp });
			return b.getApi(api);
		}

		@Override
		public CertificateAuthenticationApi getKeyAuthApi() {
			ConfigurableApiBuilder b = pab.call();
			b.setUrl(KEY_AUTH_URL);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			b.setKeyManagers(id.getKeyManagers());
			return b.getApi(CertificateAuthenticationApi.class);
		}

		@Override
		public SymphonyIdentity getIdentity() {
			return id;
		}

		@Override
		public <X> X getAgentApi(Class<X> api) throws Exception {
			ConfigurableApiBuilder b = pab.call();
			b.setUrl(AGENT_URL);
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			b.setWrappers(new ApiWrapper[] { tm, streamHelp });
			return b.getApi(api);
		}

		@Override
		public TokenManager getTokenManager() {
			return tm;
		}

		@Override
		public AuthenticationApi getRSASessionAuthApi() {
			ConfigurableApiBuilder b = pab.call();
			b.setUrl(LOGIN_URL);
			b.setKeyManagers(id.getKeyManagers());
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			return b.getApi(AuthenticationApi.class);
		}

		@Override
		public AuthenticationApi getRSAKeyAuthApi() {
			ConfigurableApiBuilder b = pab.call();
			b.setUrl(RELAY_URL);
			b.setKeyManagers(id.getKeyManagers());
			b.setProxyDetails(CI_PROXY, null, null, 8080);
			return b.getApi(AuthenticationApi.class);
		}
		
		@Override
		public ConfigurableApiBuilder getApiBuilder() {
			return pab.call();
		}
	}
	
	public static final class CertTestClientStrategy extends AbstractTestClientStrategy {
		
		public CertTestClientStrategy(SymphonyIdentity id, Producer<ConfigurableApiBuilder> pab) {
			super(id, pab);
		}

		protected TokenManager initializeTokenManager() {
			CertificateAuthenticationApi sessionAuthApi = getSessionAuthApi();
			CertificateAuthenticationApi keyAuthApi = getKeyAuthApi();
			TokenManager tm = new TokenManager(() -> sessionAuthApi.v1AuthenticatePost(), () -> keyAuthApi.v1AuthenticatePost()); 
			return tm;
		}

		@Override
		public AuthenticationApi getRSASessionAuthApi() {
			throw new UnsupportedOperationException("CertTestClientStrategy only allows cert Login");
		}

		@Override
		public AuthenticationApi getRSAKeyAuthApi() {
			throw new UnsupportedOperationException("CertTestClientStrategy only allows cert Login");
		}
	}

	public static final class RSATestClientStrategy extends AbstractTestClientStrategy {
		
		public RSATestClientStrategy(SymphonyIdentity id, Producer<ConfigurableApiBuilder> pab) {
			super(id, pab);
		}
		
		protected TokenManager initializeTokenManager() {
			AuthenticationApi sessionAuthApi = getRSASessionAuthApi();
			AuthenticationApi keyAuthApi = getRSAKeyAuthApi();
			TokenManager tm = new TokenManager(
					() -> sessionAuthApi.pubkeyAuthenticatePost(
							new AuthenticateRequest().token(createToken())), 
					
					() -> keyAuthApi.pubkeyAuthenticatePost(
							new AuthenticateRequest().token(createToken()))); 
			return tm;
		}

		private String createToken() {
			try {
				return JWTHelper.createSignedJwt(getIdentity().getCommonName(), getIdentity().getPrivateKey());
			} catch (Exception e) {
				throw new IllegalArgumentException("Couldn't create token", e);
			}
		}

		@Override
		public CertificateAuthenticationApi getSessionAuthApi() {
			throw new UnsupportedOperationException("RSATestClientStrategy only allows RSA Login");
		}


		@Override
		public CertificateAuthenticationApi getKeyAuthApi() {
			throw new UnsupportedOperationException("RSATestClientStrategy only allows RSA Login");
		}

	}

	private static final SymphonyIdentity BOT2_ID = TestIdentityProvider.getIdentity("symphony-develop-bot2-identity");
	private static final SymphonyIdentity BOT1_ID = TestIdentityProvider.getIdentity("symphony-develop-bot1-identity");
	
	private static final String CI_PROXY = System.getProperty("proxy");
	public static final String SESSION_AUTH_URL = "https://develop-api.symphony.com/sessionauth";
	public static final String KEY_AUTH_URL = "https://develop-api.symphony.com/keyauth";
	public static final String AGENT_URL = "https://develop.symphony.com/agent";
	public static final String POD_URL = "https://develop.symphony.com/pod";
	public static final String LOGIN_URL = "https://develop.symphony.com/login";
	public static final String RELAY_URL = "https://develop.symphony.com/relay";
		
	public static final TestClientStrategy JERSEY_RSA = new RSATestClientStrategy(BOT1_ID, () -> new JerseyApiBuilder());
	
	public static final TestClientStrategy CXF_CERT = new CertTestClientStrategy(BOT2_ID, () -> new CXFApiBuilder());
	
	public static final TestClientStrategy CXF_RSA = new RSATestClientStrategy(BOT1_ID, () -> new CXFApiBuilder());	

}
