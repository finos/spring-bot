package com.github.deutschebank.symphony.koreai.response;

import com.github.deutschebank.symphony.koreai.Address;

/**
 * Handles responses coming back from KoreAI.
 * 
 * @author moffrob
 *
 */
public interface KoreAIResponseHandler {
	
	public void handle(Address to, KoreAIResponse response);
	
	
}