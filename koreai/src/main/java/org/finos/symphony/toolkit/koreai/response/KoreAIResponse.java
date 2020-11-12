package org.finos.symphony.toolkit.koreai.response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Holder for responses from KoreAI REST Endpoint, 
 * and also to be returned for Symphony Freemarker data.
 * 
 * @author rodriva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoreAIResponse {
	
    private Map<String, Object> response = Collections.emptyMap();
    
    private Map<String, Object> template = Collections.emptyMap();
    
    private String messageML = "";
    
    private String symphonyTemplate = "message";
    
    private List<String> options = Collections.emptyList();
    
	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getSymphonyTemplate() {
		return symphonyTemplate;
	}

	public void setSymphonyTemplate(String symphonyTemplate) {
		this.symphonyTemplate = symphonyTemplate;
	}

	public String getMessageML() {
		return messageML;
	}

	public void setMessageML(String messageML) {
		this.messageML = messageML;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}

	public Map<String, Object> getTemplate() {
		return template;
	}

	public void setTemplate(Map<String, Object> template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return "KoreAIResponse [response=" + response + ", template=" + template + ", messageML=" + messageML + "]";
	}

    
}
