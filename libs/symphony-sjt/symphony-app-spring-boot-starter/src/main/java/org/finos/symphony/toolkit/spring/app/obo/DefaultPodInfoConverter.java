package org.finos.symphony.toolkit.spring.app.obo;

import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties.AuthMethod;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfo;
import org.springframework.util.StringUtils;

/**
 * Basic way of converting from the {@link PodInfo} details that come back from Symphony's admin console
 * when an app is registered, and PodProperties, which is the configuration we use to call OBO.
 * 
 * Override/extend this if you want to do clever things with proxies.
 * 
 * @author rob@kite9.com
 *
 */
public class DefaultPodInfoConverter implements PodInfoConverter {

	@Override
	public PodProperties convert(PodInfo podInfo) {
		if (podInfo == null) {
			return null;
		}
		PodProperties out = new PodProperties();
		out.setId(podInfo.getCompanyId());
		out.setSessionAuth(setupSessionAuth(podInfo));
		out.setPod(setupPod(podInfo));
		out.setAgent(setupAgent(podInfo));
		out.setLogin(setupLogin(podInfo));
		decideAuthMethod(out);
		return out;
	}

	protected void decideAuthMethod(PodProperties out) {
		if (StringUtils.hasText(out.getSessionAuth().getUrl())) {
			out.setAuthMethod(AuthMethod.CERT);
		} else {
			out.setAuthMethod(AuthMethod.RSA);
		}
	}

	protected EndpointProperties setupSessionAuth(PodInfo podInfo) {
		EndpointProperties out = new EndpointProperties();
		out.setUrl(setupUrl(podInfo.getPayload().getSessionAuthUrl(), podInfo.getPayload().getBaseUrl(), "/sessionauth"));
		return out;
	}
	
	private String setupUrl(String sessionAuthUrl, String baseUrl, String ext) {
		return sessionAuthUrl == null ? 
				(baseUrl != null ? baseUrl+ ext : null ) : 
				sessionAuthUrl;
	}

	protected EndpointProperties setupPod(PodInfo podInfo) {
		EndpointProperties out = new EndpointProperties();
		out.setUrl(setupUrl(podInfo.getPayload().getPodUrl(), podInfo.getPayload().getBaseUrl(), "/pod"));
		return out;
	}
	
	protected EndpointProperties setupLogin(PodInfo podInfo) {
		EndpointProperties out = new EndpointProperties();
		out.setUrl(setupUrl(podInfo.getPayload().getLoginUrl(), podInfo.getPayload().getBaseUrl(), "/login"));
		return out;
	}
	
	protected EndpointProperties setupAgent(PodInfo podInfo) {
		EndpointProperties out = new EndpointProperties();
		out.setUrl(setupUrl(podInfo.getPayload().getAgentUrl(), podInfo.getPayload().getBaseUrl(), "/agent"));
		return out;
	}

}
