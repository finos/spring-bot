package org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.AbstractElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;

public class EditActionElementsConsumer extends AbstractElementsConsumer {

	
	@Override
	public List<Response> apply(ElementsAction u) {
		Workflow wf = u.getWorkflow();
		if (u.getAction().equals(ClassBasedWorkflow.WF_EDIT)) {
			EntityJson ej = u.getData();
			Object ob = ej.get(EntityJsonConverter.WORKFLOW_001);
			return Collections.singletonList(new FormResponse(wf, u.getAddressable(), ej, 
					"Edit "+wf.getName(ob.getClass()), 
					wf.getInstructions(ob.getClass()), ob, true, 
				ButtonList.of()));
		}
		
		return null;
	}

}
