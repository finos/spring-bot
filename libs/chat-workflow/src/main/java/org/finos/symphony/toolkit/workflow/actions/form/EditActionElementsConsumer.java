package org.finos.symphony.toolkit.workflow.actions.form;

import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.util.ErrorHandler;

public class EditActionElementsConsumer extends AbstractTableActionConsumer {

	public static final String EDIT = "wf-edit";
	
	public EditActionElementsConsumer(ErrorHandler errorHandler, ResponseHandlers rh) {
		super(errorHandler, rh);
	}

	@Override
	public void acceptFormAction(FormAction u) {
		if (u.getAction().equals(EDIT)) {
			Object o = u.getData().get(WorkResponse.OBJECT_KEY);
			rh.accept(new WorkResponse(u.getAddressable(), o, WorkMode.EDIT));
		}
	}

}
