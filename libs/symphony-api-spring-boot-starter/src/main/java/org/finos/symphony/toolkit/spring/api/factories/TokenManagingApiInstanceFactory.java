package org.finos.symphony.toolkit.spring.api.factories;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;

import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.StreamIDHelp;
import com.symphony.api.bindings.TokenManager;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Handles wiring the {@link TokenManager} and the {@link StreamIDHelp} to the {@link ApiInstance}s.
 * 
 * @author robmoffat
 *
 */
public class TokenManagingApiInstanceFactory extends AbstractGenericInstanceTokenManagingFactory<SymphonyIdentity> implements ApiInstanceFactory {

	public TokenManagingApiInstanceFactory(ApiBuilderFactory apiBuilderFactory) {
		super(apiBuilderFactory);
	}
	

	protected TokenManager createTokenManager(SymphonyIdentity id, PodProperties pp, ApiBuilder sessionAuth, ApiBuilder keyAuth, ApiBuilder relay, ApiBuilder login) {
		TokenManager tm;
		if (usingCertificates(id, pp)) {
			tm = new TokenManager(() -> sessionAuth.getApi(CertificateAuthenticationApi.class).v1AuthenticatePost(),
					() -> keyAuth.getApi(CertificateAuthenticationApi.class).v1AuthenticatePost());
		} else {
			tm = new TokenManager(
					() -> login.getApi(com.symphony.api.login.AuthenticationApi.class)
							.pubkeyAuthenticatePost(createAuthenticateRequest(id)),
					() -> relay.getApi(com.symphony.api.login.AuthenticationApi.class)
							.pubkeyAuthenticatePost(createAuthenticateRequest(id)));
		}
		return tm;
	}


	@Override
	protected SymphonyIdentity getIdentity(SymphonyIdentity id) {
		return id;
	}

}