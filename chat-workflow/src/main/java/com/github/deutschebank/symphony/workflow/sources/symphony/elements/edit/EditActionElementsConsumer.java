package com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit;

import java.util.Collections;
import java.util.List;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.form.ButtonList;
import com.github.deutschebank.symphony.workflow.java.workflow.ClassBasedWorkflow;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.AbstractElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsAction;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;

public class EditActionElementsConsumer extends AbstractElementsConsumer {

	
	@Override
	public List<Response> apply(ElementsAction u) {
		Workflow wf = u.getWorkflow();
		if (u.getAction().equals(ClassBasedWorkflow.WF_EDIT)) {
			EntityJson ej = u.getData();
			Object ob = ej.get(EntityJsonConverter.WORKFLOW_001);
			return Collections.singletonList(new FormResponse(wf, u.getAddressable(), ob, 
					"Edit "+wf.getName(ob.getClass()), 
					wf.getInstructions(ob.getClass()), ob, true, 
				ButtonList.of()));
		}
		
		return null;
	}

}
