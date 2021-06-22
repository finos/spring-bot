package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.workflow.CommandPerformer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.response.Response;

public class MethodCallElementsConsumer extends AbstractElementsConsumer implements ElementsConsumer {

	CommandPerformer cp;
	
	public MethodCallElementsConsumer(CommandPerformer cp) {
		super();
		this.cp = cp;
	}

	@Override
	public List<Response> apply(ElementsAction u) {
		String verb = u.getAction();
		Workflow wf = u.getWorkflow();
		Addressable a = u.getAddressable();
		
		if ((verb.endsWith("+0")) || (verb.endsWith("+1"))) {
			// argument provided
			verb = verb.substring(0, verb.length()-2);
		}
		
		if (wf.hasMatchingCommand(verb, a)) {
			return cp.applyCommand(verb, u);
		} else {
			return Collections.emptyList();
		}
	}

}
