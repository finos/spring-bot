package org.finos.symphony.toolkit.koreai.spring;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Stores configuration settings for koreai connectivity.  This supports 
 * configuring multiple pairs of symphony/koreAI bots.
 * 
 * @author rodriva
 */
@ConfigurationProperties("symphony.koreai")
public class KoreAIProperties {

	private String templatePrefix = "file:/koreai/templates";

	private List<KoreAIInstanceProperties> instances;
	
	public List<KoreAIInstanceProperties> getInstances() {
		return instances;
	}

	public void setInstances(List<KoreAIInstanceProperties> instances) {
		this.instances = instances;
	}

	public String getTemplatePrefix() {
		return templatePrefix;
	}

	public void setTemplatePrefix(String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}
}
