package org.finos.springbot.symphony;

@SuppressWarnings("serial")
public class SymphonyException extends RuntimeException {

	public SymphonyException(String message, Throwable cause) {
		super(message, cause);
	}

	public SymphonyException(String message) {
		super(message);
	}

	public SymphonyException(Throwable cause) {
		super(cause);
	}

}
