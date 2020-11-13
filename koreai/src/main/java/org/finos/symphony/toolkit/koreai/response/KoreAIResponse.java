package org.finos.symphony.toolkit.koreai.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;	
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class contains the JSON returned from the KoreAI call, (original) 
 * and a post-processed version which is more useful for FreeMarker templates in symphony.
 * 
 * @author rodriva, robmoffat
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoreAIResponse {
	
	public static final String MESSAGE_ML = "messageML";

	public static final String TEMPLATE_TYPE = "template_type";

	public static final String TEXT = "text";

	
	private JsonNode original;
    
    private List<ObjectNode> processed;


	public JsonNode getOriginal() {
		return original;
	}

	public void setOriginal(JsonNode original) {
		this.original = original;
	}

	public List<ObjectNode> getProcessed() {
		return processed;
	}

	public void setProcessed(List<ObjectNode> processed) {
		this.processed = processed;
	}
    
    
}
