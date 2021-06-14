package org.finos.symphony.rssbot;

import java.util.List;

import org.finos.symphony.toolkit.spring.api.properties.ProxyProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.rss")
public class RSSProperties {

	List<ProxyProperties> proxies;
	
	public void setProxies(List<ProxyProperties> proxies) {
		this.proxies = proxies;
	}

	String successMessage = "Feed loaded and configured successfully.";
	
	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public String getObservationStreamId() {
		return observationStreamId;
	}

	public void setObservationStreamId(String observationStream) {
		this.observationStreamId = observationStream;
	}

	String failureMessage = "<p>There has been a problem configuring this RSS-Feed.</p>"+
			"<p>Please talk to the Symphony Administrators for assistance</p>";
	
	String observationStreamId = null;		// means don't use

	public List<ProxyProperties> getProxies() {
		return proxies;
	}

	public void setProxy(List<ProxyProperties> proxy) {
		this.proxies = proxy;
	}
}
