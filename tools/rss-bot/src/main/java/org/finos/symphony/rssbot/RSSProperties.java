package org.finos.symphony.rssbot;

import org.finos.symphony.toolkit.spring.api.properties.ProxyProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.rss")
public class RSSProperties {

	ProxyProperties proxy;

	public ProxyProperties getProxy() {
		return proxy;
	}

	public void setProxy(ProxyProperties proxy) {
		this.proxy = proxy;
	}
}
