package com.github.deutschebank.symphony.stream.integration;

import java.util.function.Supplier;

import org.junit.Before;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.JWTHelper;
import com.symphony.api.bindings.StreamIDHelp;
import com.symphony.api.bindings.TokenManager;
import com.symphony.api.bindings.cxf.CXFApiBuilder;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;
import com.symphony.api.login.AuthenticationApi;
import com.symphony.api.model.AuthenticateRequest;
import com.symphony.api.model.Token;
import com.symphony.api.pod.StreamsApi;

public class AbstractTest {

	private static final String CI_PROXY = System.getProperty("proxy");
	public static final String SESSION_AUTH_URL = "https://develop-api.symphony.com/sessionauth";
	public static final String KEY_AUTH_URL = "https://develop-api.symphony.com/keyauth";
	public static final String AGENT_URL = "https://develop.symphony.com/agent";
	public static final String POD_URL = "https://develop.symphony.com/pod";
	public static final String LOGIN_URL = "https://develop.symphony.com/login";
	public static final String RELAY_URL = "https://develop.symphony.com/relay";
	private static final SymphonyIdentity ID = TestIdentityProvider.getTestIdentity();

	public AuthenticationApi getRSASessionAuthApi() {
		ConfigurableApiBuilder b = new CXFApiBuilder();
		b.setUrl(LOGIN_URL);
		b.setKeyManagers(ID.getKeyManagers());
		b.setProxyDetails(CI_PROXY, null, null, 8080);
		return b.getApi(AuthenticationApi.class);
	}

	public AuthenticationApi getRSAKeyAuthApi() {
		ConfigurableApiBuilder b = new CXFApiBuilder();
		b.setUrl(RELAY_URL);
		b.setKeyManagers(ID.getKeyManagers());
		b.setProxyDetails(CI_PROXY, null, null, 8080);
		return b.getApi(AuthenticationApi.class);
	}
	
	protected TokenManager initializeTokenManager() {
		AuthenticationApi sessionAuthApi = getRSASessionAuthApi();
		AuthenticationApi keyAuthApi = getRSAKeyAuthApi();
		TokenManager tm = new TokenManager(
				getSessionToken(sessionAuthApi), 
				getKeyManagerToken(keyAuthApi)); 
		return tm;
	}

	protected Supplier<Token> getKeyManagerToken(AuthenticationApi keyAuthApi) {
		return () -> keyAuthApi.pubkeyAuthenticatePost(
				new AuthenticateRequest().token(createToken()));
	}

	protected Supplier<Token> getSessionToken(AuthenticationApi sessionAuthApi) {
		return () -> sessionAuthApi.pubkeyAuthenticatePost(
				new AuthenticateRequest().token(createToken()));
	}
	

	private String createToken() {
		try {
			return JWTHelper.createSignedJwt(ID.getCommonName(), ID.getPrivateKey());
		} catch (Exception e) {
			throw new IllegalArgumentException("Couldn't create token", e);
		}
	}
	
	protected TokenManager tm;
	
	protected StreamsApi streamsApi;
	
	protected MessagesApi messagesApi;
	
	protected StreamIDHelp streamHelp = new StreamIDHelp();
		
	public <X> X getAgentApi(Class<X> api) throws Exception {
		ConfigurableApiBuilder b = new CXFApiBuilder();
		b.setUrl(AGENT_URL);
		b.setProxyDetails(CI_PROXY, null, null, 8080);
		b.setWrappers(new ApiWrapper[] { tm, streamHelp });
		return b.getApi(api);
	}

	@Before
	public void setupApis() throws Exception {
		tm = initializeTokenManager();
		streamsApi = getAgentApi(StreamsApi.class);
		messagesApi = getAgentApi(MessagesApi.class);
	}
		
}
