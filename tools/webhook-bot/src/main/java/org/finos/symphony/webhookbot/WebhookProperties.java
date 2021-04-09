package org.finos.symphony.webhookbot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.webhook")
public class WebhookProperties {

	String baseUrl = "http://l";

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
}
