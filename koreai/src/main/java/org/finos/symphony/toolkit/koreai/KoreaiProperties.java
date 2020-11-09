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
	
	private boolean skipEmptyResponses;

	private String formTemplate = "classpath:/templates/koreai-form.ftl";

	private String messageTemplate = "classpath:/templates/koreai-message.ftl";

	public String getFormTemplate() {
		return formTemplate;
	}

	public void setFormTemplate(String template) {
		this.formTemplate = template;
	}

	public String getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(String template) {
		this.messageTemplate = template;
	}

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

}
