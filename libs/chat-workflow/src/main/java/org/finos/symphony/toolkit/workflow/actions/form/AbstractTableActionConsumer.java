package org.finos.symphony.toolkit.workflow.actions.form;


import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.actions.consumers.AbstractActionConsumer;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.util.ErrorHandler;

public abstract class AbstractTableActionConsumer extends AbstractActionConsumer {
	
	public static final String WORKFLOW_001 = "workflow_001";
	
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
