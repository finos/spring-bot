package org.finos.symphony.toolkit.stream;

public class SharedStreamException extends RuntimeException {

	private static final long serialVersionUID = 8791238818865993828L;

	public SharedStreamException() {
		super();
	}

	public SharedStreamException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SharedStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public SharedStreamException(String message) {
		super(message);
	}

	public SharedStreamException(Throwable cause) {
		super(cause);
	}

	
}
