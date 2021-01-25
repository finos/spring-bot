package org.finos.symphony.toolkit.spring.api;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties.AuthMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.JWTHelper;
import com.symphony.api.bindings.StreamIDHelp;
import com.symphony.api.bindings.TokenManager;
import com.symphony.api.id.IdentityConfigurationException;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.AuthenticateRequest;

/**
 * Handles wiring the {@link TokenManager} and the {@link StreamIDHelp} to the {@link ApiInstance}s.
 * 
 * @author robmoffat
 *
 */
public class TokenManagingApiInstanceFactory extends AbstractApiInstanceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultApiInstanceFactory.class);

	public TokenManagingApiInstanceFactory(ApiBuilderFactory apiBuilderFactory) {
		super(apiBuilderFactory);
	}
	
	@Override
	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception {
		ApiBuilder sessionAuthApiBuilder = createApiBuilder(pp, pp.getSessionAuth(), id, trustManagers, "sessionAuth");
		ApiBuilder keyAuthApiBuilder = createApiBuilder(pp, pp.getKeyAuth(), id, trustManagers, "keyAuth");
		ApiBuilder relayApiBuilder = createApiBuilder(pp, pp.getRelay(), id, trustManagers, "relay");
		ApiBuilder loginApiBuilder = createApiBuilder(pp, pp.getLogin(), id, trustManagers, "login");
		
		TokenManager tm = createTokenManager(id, pp, sessionAuthApiBuilder, keyAuthApiBuilder, relayApiBuilder, loginApiBuilder);
	
		ApiBuilder podApiBuilder = createApiBuilder(pp, pp.getPod(), tm, id, trustManagers);
		ApiBuilder agentApiBuilder = createApiBuilder(pp, pp.getAgent(), tm, id, trustManagers);
				
		ApiInstance out = new BasicAPIInstance(
				sessionAuthApiBuilder, 
				keyAuthApiBuilder,
				relayApiBuilder,
				loginApiBuilder,
				podApiBuilder,
				agentApiBuilder,
				id);
		
		return out;
				
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

	private boolean usingCertificates(SymphonyIdentity botIdentity, PodProperties pp) {
		if (pp.getAuthMethod() == AuthMethod.CERT) {
			LOG.info("Bot "+botIdentity.getCommonName()+" authentication = CERT");
			return true;
		}
		
		if (pp.getAuthMethod() == AuthMethod.RSA) {
			LOG.info("Bot "+botIdentity.getCommonName()+" authentication = RSA");
			return false;
		}
		
		boolean hasCerts = (botIdentity.getCertificateChain() != null) && (botIdentity.getCertificateChain().length > 0);
		LOG.info("Bot "+botIdentity.getCommonName()+" using certs? {} ", hasCerts);
	
		return hasCerts;
	}

	/**
	 * Override this method to configure your own ApiBuilder implementation.
	 */
	protected ConfigurableApiBuilder createApiBuilder(PodProperties pp, EndpointProperties ep, TokenManager tm, SymphonyIdentity id, TrustManager[] trustManagers) throws Exception {
		ConfigurableApiBuilder ab = apiBuilderFactory.getObject();
		List<ApiWrapper> wrappers = new ArrayList<>();
		wrappers.add(tm);
		List<ApiWrapper> extraWrappers = buildApiWrappers(pp, id, ep);
		wrappers.addAll(extraWrappers);
		ApiWrapper[] wrapperArray = wrappers.stream().toArray(s -> new ApiWrapper[s]);
		ep.configure(ab, wrapperArray, trustManagers);
		return ab;
	}

	/**
	 * Override this method to change the wrappers used.  By default, this just returns the {@link StreamIDHelp}
	 * wrapper, which you probably want to keep.
	 */
	protected List<ApiWrapper> buildApiWrappers(PodProperties pp, SymphonyIdentity id, EndpointProperties ep) {
		List<ApiWrapper> out = new ArrayList<>();
		out.add(new StreamIDHelp());
		return out;
	}

	/**
	 * Override this method to configure your own ApiBuilder implementation.
	 */
	protected ConfigurableApiBuilder createApiBuilder(PodProperties pp, EndpointProperties ep, SymphonyIdentity id, TrustManager[] trustManagers, String apiName) throws Exception {
		if (ep == null) {
			LOG.warn("symphony.apis[{}].{} not set: could cause NPE when doing get{}Api()", pp.getId(),
					apiName.toLowerCase(), StringUtils.capitalize(apiName));
			return null;
		}

		List<ApiWrapper> wrappers = buildApiWrappers(pp, id, ep);
		ApiWrapper[] wrapperArray = wrappers.stream().toArray(s -> new ApiWrapper[s]);
		ConfigurableApiBuilder ab = apiBuilderFactory.getObject();
		ep.configure(ab, wrapperArray, id, trustManagers);
		return ab;
	}

	private AuthenticateRequest createAuthenticateRequest(SymphonyIdentity botIdentity) {
		try {
			return new AuthenticateRequest()
					.token(JWTHelper.createSignedJwt(botIdentity.getCommonName(), botIdentity.getPrivateKey()));
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't create AuthenticationRequest", e);
		}
	}

}