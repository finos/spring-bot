package com.github.deutschebank.symphony.spring.app.tokens.pod;

import java.util.Map;

import javax.net.ssl.TrustManager;

import org.springframework.util.StringUtils;

import com.github.deutschebank.symphony.spring.api.builders.ApiBuilderFactory;
import com.github.deutschebank.symphony.spring.api.properties.PodProperties;
import com.github.deutschebank.symphony.spring.api.properties.SymphonyApiProperties;
import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.AppAuthenticateRequest;
import com.symphony.api.model.ExtensionAppTokens;
import com.symphony.api.pod.PodApi;

/**
 * This uses pods configured in spring to decide which pod to connect to.  
 * If you aren't running in Symphony's main market, then this is the way to go.  
 * 
 * @author Rob Moffat
 *
 */
public class ConfiguredPodTokenStrategy extends AbstractPodTokenStrategy<PodProperties> {
	
	protected SymphonyApiProperties apiProperties;
	
	public ConfiguredPodTokenStrategy(SymphonyApiProperties apiProperties, SymphonyIdentity appIdentity,
			ApiBuilderFactory abf, TrustManager[] trustManagers) {
		super(appIdentity, abf, trustManagers);
		this.apiProperties = apiProperties;
	}

	protected ExtensionAppTokens certBasedRequest(String appToken, PodProperties pod) throws Exception {
		ConfigurableApiBuilder ab = abf.getObject();
		pod.getSessionAuth().configure(ab, new ApiWrapper[] {}, appIdentity, trustManagers);
		AuthenticationApi aa = ab.getApi(AuthenticationApi.class);
		AppAuthenticateRequest ar = new AppAuthenticateRequest();
		ar.setAppToken(appToken);
		ExtensionAppTokens out = aa.v1AuthenticateExtensionAppPost(ar);
		return out;
	}

	protected PodProperties getPodProperties(String id) {
		if (!StringUtils.isEmpty(id)) {
			for (PodProperties pp : apiProperties.getApis()) {
				if (id.equals(pp.getId())) {
					return pp;
				}
			}
		} 

		return null;
	}

	@Override
	protected PodApi getPodApi(Map<String, Object> claims) throws Exception {
		String companyId = getCompanyId(claims);
		PodProperties pp = getPodProperties(companyId);
		if (pp == null) {
			return null;
		}
		
		ConfigurableApiBuilder ab = abf.getObject();
		pp.getPod().configure(ab, new ApiWrapper[] {}, appIdentity, trustManagers);
		return ab.getApi(PodApi.class);
	}

}
