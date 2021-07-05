package org.finos.symphony.toolkit.workflow.message;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.ResponseHandler;
import org.springframework.util.ErrorHandler;

public class ChatWorkflowErrorHandler implements ErrorHandler {

	ResponseHandler rh;
	String templateName;
	
	public ChatWorkflowErrorHandler(ResponseHandler rh, String templateName) {
		super();
		this.rh = rh;
		this.templateName = templateName;
	}

	@Override
	public void handleError(Throwable t) {
		Action currentAction = Action.CURRENT_ACTION.get();
		ErrorResponse er = new ErrorResponse(currentAction.getAddressable(), t, templateName);
		rh.accept(er);
	}

}
