package org.finos.symphony.toolkit.workflow.actions.consumers;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class ChatWorkflowErrorHandler implements ErrorHandler {
	
	protected static final Logger LOG = LoggerFactory.getLogger(ChatWorkflowErrorHandler.class);


	ResponseHandlers rh;
	String templateName;
	
	public ChatWorkflowErrorHandler(ResponseHandlers rh, String templateName) {
		super();
		this.rh = rh;
		this.templateName = templateName;
	}

	@Override
	public void handleError(Throwable t) {
		LOG.error("Error thrown:" , t);
		Action currentAction = Action.CURRENT_ACTION.get();
		if (currentAction != null) {
			ErrorResponse er = new ErrorResponse(currentAction.getAddressable(), t, templateName);
		
			try {
				rh.accept(er);
			} catch (Throwable e) {
				LOG.warn("Couldn't return error {} due to error {} ", er, e);
			}
		}
	}

}
