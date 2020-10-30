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
    
    private String template = "classpath:/templates/koreai-form.ftl";
    
    public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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
}
