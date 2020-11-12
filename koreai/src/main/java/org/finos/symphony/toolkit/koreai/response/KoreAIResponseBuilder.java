package org.finos.symphony.toolkit.koreai.response;

/**
 * Turns the response from KoreAI into a {@link KoreAIResponse} object.
 */
public interface KoreAIResponseBuilder {

	public KoreAIResponse formatResponse(String json) throws Exception;
	
}

