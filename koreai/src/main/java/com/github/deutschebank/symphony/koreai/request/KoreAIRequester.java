package com.github.deutschebank.symphony.koreai.request;

import com.github.deutschebank.symphony.koreai.Address;

public interface KoreAIRequester {

	/**
	 * Send a plain-text message to Kore AI.
	 */
	public void send(Address from, String message);
}
