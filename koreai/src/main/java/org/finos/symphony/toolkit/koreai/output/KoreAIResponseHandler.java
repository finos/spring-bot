package org.finos.symphony.toolkit.koreai.output;

import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;

/**
 * Handles responses coming back from KoreAI.
 * 
 * @author moffrob
 *
 */
public interface KoreAIResponseHandler {
	
	public void handle(Address to, KoreAIResponse response);
	
	
}