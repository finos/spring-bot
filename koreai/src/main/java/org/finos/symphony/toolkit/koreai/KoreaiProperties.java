package org.finos.symphony.toolkit.koreai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Stores configuration settings for koreai connectivity.
 * 
 * @author rodriva
 */
@ConfigurationProperties("symphony.koreai")
public class KoreaiProperties {

	private String jwt;

	private String url;
	
	private boolean skipEmptyResponses = false;
	
	private boolean onlyAddressed = false;
	
	private String templatePrefix = "classpath:/koreai/templates";

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

	public boolean isSkipEmptyResponses() {
		return skipEmptyResponses;
	}

	public void setSkipEmptyResponses(boolean skipEmptyResponses) {
		this.skipEmptyResponses = skipEmptyResponses;
	}

	public boolean isOnlyAddressed() {
		return onlyAddressed;
	}

	public void setOnlyAddressed(boolean onlyAddressed) {
		this.onlyAddressed = onlyAddressed;
	}

	public String getTemplatePrefix() {
		return templatePrefix;
	}

	public void setTemplatePrefix(String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}
}
