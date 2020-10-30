package org.finos.symphony.toolkit.koreai.response;

import org.finos.symphony.toolkit.koreai.Address;

/**
 * Handles responses coming back from KoreAI.
 * 
 * @author moffrob
 *
 */
public interface KoreAIResponseHandler {
	
	public void handle(Address to, KoreAIResponse response);
	
	
}