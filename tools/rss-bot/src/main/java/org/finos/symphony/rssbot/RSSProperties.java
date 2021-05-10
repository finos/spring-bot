package org.finos.symphony.rssbot;

import java.util.List;

import org.finos.symphony.toolkit.spring.api.properties.ProxyProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.rss")
public class RSSProperties {

	List<ProxyProperties> proxies;

	public List<ProxyProperties> getProxies() {
		return proxies;
	}

	public void setProxy(List<ProxyProperties> proxy) {
		this.proxies = proxy;
	}
}
