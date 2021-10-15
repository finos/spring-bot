package org.finos.springbot.workflow.actions.form;


import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.consumers.AbstractActionConsumer;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.util.ErrorHandler;

public abstract class AbstractTableActionConsumer extends AbstractActionConsumer {
	
	protected static final String WORKFLOW_001 = "workflow_001";
	
	protected ResponseHandlers rh;
	

	public AbstractTableActionConsumer(ErrorHandler errorHandler, ResponseHandlers rh) {
		super(errorHandler);
		this.rh = rh;
	}


	@Override
	public void accept(Action t) {
		if (t instanceof FormAction) {
			acceptFormAction((FormAction)t);
		}
	}

	protected abstract void acceptFormAction(FormAction fa);
	
}
