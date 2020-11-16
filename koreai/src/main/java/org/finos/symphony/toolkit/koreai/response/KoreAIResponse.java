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

	public static final String OPTIONS_ML = "optionsML";

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((original == null) ? 0 : original.hashCode());
		result = prime * result + ((processed == null) ? 0 : processed.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KoreAIResponse other = (KoreAIResponse) obj;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		if (processed == null) {
			if (other.processed != null)
				return false;
		} else if (!processed.equals(other.processed))
			return false;
		return true;
	}
    
    
	
}
