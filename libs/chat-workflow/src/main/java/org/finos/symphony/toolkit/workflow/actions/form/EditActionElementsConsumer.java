package org.finos.symphony.toolkit.workflow.actions.form;

import java.util.Collections;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.util.ErrorHandler;

public class EditActionElementsConsumer extends AbstractTableActionConsumer {

	public static final String EDIT = "Edit";
	
	public EditActionElementsConsumer(ErrorHandler errorHandler, ResponseHandlers rh) {
		super(errorHandler, rh);
	}

	@Override
	public void acceptFormAction(FormAction u) {
		if (u.getAction().equals(EDIT)) {
			Object o = u.getData();
//			Object ob = ej.get(FormAction.);
//			return Collections.singletonList(new FormResponse(wf, u.getAddressable(), ej, 
//					"Edit "+wf.getName(ob.getClass()), 
//					wf.getInstructions(ob.getClass()), ob, true, 
//				ButtonList.of()));
		}
		
		//return null;
	}

}
