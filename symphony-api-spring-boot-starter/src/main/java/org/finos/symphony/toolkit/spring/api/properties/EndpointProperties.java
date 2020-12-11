package org.finos.symphony.toolkit.spring.api.properties;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManager;

import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Encapsulates the details needed to connect to one of the Symphony endpoints.
 * 
 * @author Rob Moffat
 *
 */
public class EndpointProperties {

	private String url;
	
	private ProxyProperties proxy;
	
	private List<ProxyProperties> proxies;
	
	private String testUrl;
	
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
	public List<ProxyProperties> getProxies() {
		return proxies;
	}
	public void setProxies(List<ProxyProperties> proxies) {
		this.proxies = proxies;
	}
	public String getTestUrl() {
		return testUrl;
	}
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	public void configure(ConfigurableApiBuilder ab, ApiWrapper[] wrappers, SymphonyIdentity id, TrustManager[] trustManagers) {
		ab.setUrl(getUrl());
		ab.setKeyManagers(id.getKeyManagers());
		ab.setTrustManagers(trustManagers);
		ab.setWrappers(wrappers);
		configureProxy(ab);
	}
	public void configureProxy(ConfigurableApiBuilder ab) {
		List<ProxyProperties> allProxies = new ArrayList<ProxyProperties>();
		
		
		if (getProxy() != null) {
			allProxies.add(getProxy());
		}
		
		if (getProxies() != null) {
			allProxies.addAll(getProxies());
		}
		
		String testUrl = getTestUrl() == null ? getUrl() : getTestUrl();
		
		if (allProxies.size() == 0) {
			return;
		} else if (allProxies.size() == 1) {
			allProxies.get(0).configure(ab);
		} else {
			// determine a working proxy
			
			for (ProxyProperties proxyProperties : allProxies) {
				proxyProperties.configure(ab);
				if (ab.testConnection(testUrl)) {
					return;
				}
			}
			
			throw new RuntimeException("None of the configured proxies can connect to "+testUrl);
			
		}
	}
	
	public void configure(ConfigurableApiBuilder ab, ApiWrapper[] apiWrappers, TrustManager[] trustManagers) {
		ab.setUrl(getUrl());
		ab.setWrappers(apiWrappers);
		ab.setTrustManagers(trustManagers);
		configureProxy(ab);
	}
	
}

