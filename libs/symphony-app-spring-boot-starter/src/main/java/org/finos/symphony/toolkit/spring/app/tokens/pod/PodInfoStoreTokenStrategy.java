package org.finos.symphony.toolkit.spring.app.tokens.pod;

import java.util.Map;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.ProxyProperties;
import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfo;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfoStore;

import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.ExtensionAppAuthenticateRequest;
import com.symphony.api.model.ExtensionAppTokens;
import com.symphony.api.pod.PodApi;

/**
 * Tries returning tokens from pod details stored in the {@link PodInfoStore}.
 * 
 * @author Rob Moffat
 *
 */
public class PodInfoStoreTokenStrategy extends AbstractPodTokenStrategy<PodInfo> {

	PodInfoStore store;
	SymphonyAppProperties appProperties;
	
	public PodInfoStoreTokenStrategy(SymphonyAppProperties appProperties, SymphonyIdentity appIdentity,
			ApiBuilderFactory abf, TrustManager[] trustManagers, PodInfoStore store) {
		super(appIdentity, abf, trustManagers);
		this.store = store;
		this.appProperties = appProperties;
	}

	@Override
	protected PodInfo getPodProperties(String podId) {
		PodInfo pi = store.getPodInfo(podId);
		return pi;
	}

	@Override
	protected ExtensionAppTokens certBasedRequest(String appToken, PodInfo pod) throws Exception {
		ApiBuilder ab = setupApiBuilder(pod, pod.getPayload().getSessionAuthUrl());
		CertificateAuthenticationApi aa = ab.getApi(CertificateAuthenticationApi.class);
		ExtensionAppAuthenticateRequest ar = new ExtensionAppAuthenticateRequest();
		ar.setAppToken(appToken);
		ExtensionAppTokens out = aa.v1AuthenticateExtensionAppPost(ar);
		return out;

	}

	private ApiBuilder setupApiBuilder(PodInfo pod, String url) throws Exception {
		ConfigurableApiBuilder ab = abf.getObject();
		ab.setUrl(url);
		ab.setKeyManagers(appIdentity.getKeyManagers());
		ab.setTrustManagers(trustManagers);
		if (pod.getUseProxy() != Boolean.FALSE) {
			ProxyProperties proxy = appProperties.getProxy();
			if (proxy != null) {
				proxy.configure(ab);
			}
		}
		return ab;
	}

	@Override
	protected PodApi getPodApi(Map<String, Object> claims) throws Exception {
		String companyId = getCompanyId(claims);
		PodInfo pi = getPodProperties(companyId);
		if (pi == null) {
			return null;
		}
		
		ApiBuilder ab =  setupApiBuilder(pi, pi.getPayload().getPodUrl());
		return ab.getApi(PodApi.class);	}

}
