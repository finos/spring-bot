package org.finos.symphony.toolkit.workflow.actions.consumers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.util.ErrorHandler;

public class ChatWorkflowErrorHandler implements ErrorHandler {
	
	protected static final Log logger = LogFactory.getLog(ChatWorkflowErrorHandler.class);


	ResponseHandlers rh;
	String templateName;
	
	public ChatWorkflowErrorHandler(ResponseHandlers rh, String templateName) {
		super();
		this.rh = rh;
		this.templateName = templateName;
	}

	@Override
	public void handleError(Throwable t) {
		logger.error("Error thrown:" , t);
		Action currentAction = Action.CURRENT_ACTION.get();
		ErrorResponse er = new ErrorResponse(currentAction.getAddressable(), t, templateName);
		rh.accept(er);
	}

}
