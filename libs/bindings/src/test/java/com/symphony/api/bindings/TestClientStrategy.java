package com.symphony.api.bindings;

import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.login.AuthenticationApi;

/**
 * Using a subclass of this will either give you Jersey or Apache CXF clients.
 */
public interface TestClientStrategy {

	public CertificateAuthenticationApi getSessionAuthApi();

	public CertificateAuthenticationApi getKeyAuthApi();

	public <X> X getAgentApi(Class<X> api) throws Exception;

	public <X> X getPodApi(Class<X> api) throws Exception;
	
	public SymphonyIdentity getIdentity();
	
	public TokenManager getTokenManager();

	public AuthenticationApi getRSASessionAuthApi();
	
	public AuthenticationApi getRSAKeyAuthApi();

	public ConfigurableApiBuilder getApiBuilder();

}
