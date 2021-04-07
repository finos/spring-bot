package org.finos.symphony.toolkit.spring.api.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties.AuthMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.JWTHelper;
import com.symphony.api.bindings.StreamIDHelp;
import com.symphony.api.id.IdentityConfigurationException;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.AuthenticateRequest;

/**
 * Wraps useful methods for creating ApiInstanceFactories. 
 * 
 * @author rob@kite9.com
 *
 * @param <ID> Type of ID used to identify the user.
 */
public abstract class AbstractGenericInstanceFactory<ID> implements GenericInstanceFactory<ID> {

	protected static final Logger LOG = LoggerFactory.getLogger(DefaultApiInstanceFactory.class);
	protected ApiBuilderFactory apiBuilderFactory;

	public AbstractGenericInstanceFactory(ApiBuilderFactory apiBuilderFactory) {
		super();
		this.apiBuilderFactory = apiBuilderFactory;
	}

	protected boolean usingCertificates(SymphonyIdentity botIdentity, PodProperties pp) {
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

	protected AuthenticateRequest createAuthenticateRequest(SymphonyIdentity botIdentity) {
		try {
			return new AuthenticateRequest()
					.token(JWTHelper.createSignedJwt(botIdentity.getCommonName(), botIdentity.getPrivateKey()));
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't create AuthenticationRequest", e);
		}
	}

	/**
	 * Override this method to change the wrappers used.  By default, this just returns the {@link StreamIDHelp}
	 * wrapper, which you probably want to keep.
	 */
	protected List<ApiWrapper> buildApiWrappers(PodProperties pp, ID id, EndpointProperties ep) {
		List<ApiWrapper> out = new ArrayList<>();
		out.add(new StreamIDHelp());
		return out;
	}

	/**
	 * Override this method to configure your own ApiBuilder implementation.
	 */
	protected ConfigurableApiBuilder createApiBuilder(PodProperties pp, EndpointProperties ep, ID id, TrustManager[] trustManagers, String apiName, ApiWrapper... extra) throws Exception {
		if (ep == null) {
			LOG.warn("symphony.apis[{}].{} not set: could cause NPE when doing get{}Api()", pp.getId(),
					apiName.toLowerCase(), StringUtils.capitalize(apiName));
			return null;
		}
	
		List<ApiWrapper> wrappers = buildApiWrappers(pp, id, ep);
		Arrays.stream(extra).forEach(w -> wrappers.add(w));
		ApiWrapper[] wrapperArray = wrappers.stream().toArray(s -> new ApiWrapper[s]);
		ConfigurableApiBuilder ab = apiBuilderFactory.getObject();
		ep.configure(ab, wrapperArray, getIdentity(id), trustManagers);
		return ab;
	}

	protected abstract SymphonyIdentity getIdentity(ID id);


}