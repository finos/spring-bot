package org.finos.springbot.workflow.actions.consumers;

import org.springframework.util.ErrorHandler;

public abstract class AbstractActionConsumer implements ActionConsumer {

	protected ErrorHandler errorHandler;

	public AbstractActionConsumer(ErrorHandler errorHandler) {
		super();
		this.errorHandler = errorHandler;
	}
	
	
}
