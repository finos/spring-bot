package com.symphony.spring.api.properties;

import javax.net.ssl.TrustManager;
import javax.validation.constraints.NotNull;

import com.symphony.api.ApiWrapper;
import com.symphony.api.ConfigurableApiBuilder;
import com.symphony.id.SymphonyIdentity;

/**
 * Encapsulates the details needed to connect to one of the Symphony endpoints.
 * 
 * @author Rob Moffat
 *
 */
public class EndpointProperties {

	@NotNull private String url;
	
	private ProxyProperties proxy;
	

	
	public ProxyProperties getProxy() {
		return proxy;
	}
	public void setProxy(ProxyProperties proxy) {
		this.proxy = proxy;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	public void configure(ConfigurableApiBuilder ab, ApiWrapper[] wrappers, SymphonyIdentity id, TrustManager[] trustManagers) {
		ab.setUrl(getUrl());
		ab.setKeyManagers(id.getKeyManagers());
		ab.setTrustManagers(trustManagers);
		ab.setWrappers(wrappers);
		
		if (proxy != null) {
			proxy.configure(ab);
		}
	}
	
	public void configure(ConfigurableApiBuilder ab, ApiWrapper[] apiWrappers, TrustManager[] trustManagers) {
		ab.setUrl(getUrl());
		ab.setWrappers(apiWrappers);
		ab.setTrustManagers(trustManagers);
		
		if (proxy != null) {
			proxy.configure(ab);
		}
	}
	
}

