package com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit;

import java.util.Collections;
import java.util.List;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.java.ClassBasedWorkflow;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.AbstractElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsAction;

public class EditActionElementsConsumer extends AbstractElementsConsumer {

	
	@Override
	public List<Response> apply(ElementsAction u) {
		Workflow wf = u.getWorkflow();
		if (u.getAction().equals(ClassBasedWorkflow.WF_EDIT)) {
			
			Object ob = u.getWorkflowObject();
			return Collections.singletonList(new FormResponse(wf, u.getAddressable(), ob, "Edit "+ClassBasedWorkflow.getName(ob.getClass()), ClassBasedWorkflow.getInstructions(ob.getClass()), u.getWorkflowObject(), true, 
				Collections.emptyList()));
		}
		
		return null;
	}

}
