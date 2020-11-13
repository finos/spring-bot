package org.finos.symphony.toolkit.koreai.response;

/**
 * Turns the response from KoreAI into a {@link KoreAIResponse} object.
 * There is a lot of work going on here to "standardize" all the weird different
 * formats of Kore AI responses, and also convert them to messageML.
 */
public interface KoreAIResponseBuilder {

	public KoreAIResponse formatResponse(String json) throws Exception;
	
}

