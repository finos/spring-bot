package com.symphony.api.bindings;

import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.bindings.TokenManager;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Using a subclass of this will either give you Jersey or Apache CXF clients.
 */
public interface TestClientStrategy {

	public AuthenticationApi getSessionAuthApi();

	public AuthenticationApi getKeyAuthApi();

	public <X> X getAgentApi(Class<X> api) throws Exception;

	public <X> X getPodApi(Class<X> api) throws Exception;
	
	public SymphonyIdentity getIdentity();
	
	public TokenManager getTokenManager();

	public com.symphony.api.login.AuthenticationApi getRSASessionAuthApi();
	
	public com.symphony.api.login.AuthenticationApi getRSAKeyAuthApi();

	public ConfigurableApiBuilder getAPiBuilder();

}
