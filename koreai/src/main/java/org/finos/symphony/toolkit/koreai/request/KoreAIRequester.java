package org.finos.symphony.toolkit.koreai.request;

import org.finos.symphony.toolkit.koreai.Address;

public interface KoreAIRequester {

	/**
	 * Send a plain-text message to Kore AI.
	 */
	public void send(Address from, String message);
}
