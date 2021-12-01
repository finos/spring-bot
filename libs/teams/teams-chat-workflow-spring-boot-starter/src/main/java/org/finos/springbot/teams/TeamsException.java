package org.finos.springbot.teams;

@SuppressWarnings("serial")
public class TeamsException extends RuntimeException {

	public TeamsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TeamsException(String message) {
		super(message);
	}

	public TeamsException(Throwable cause) {
		super(cause);
	}

}
