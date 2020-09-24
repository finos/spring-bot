package com.github.deutschebank.symphony.workflow.sources.symphony.elements;

import java.util.List;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.response.Response;

public class MethodCallElementsConsumer extends AbstractElementsConsumer implements ElementsConsumer {

	
	@Override
	public List<Response> apply(ElementsAction u) {
		String verb = u.getAction();
		Workflow wf = u.getWorkflow();
		Addressable a = u.getAddressable();
		
		if (verb.endsWith("+0")) {
			// argument provided
			verb = verb.substring(0, verb.length()-2);
			return wf.applyCommand(u.getUser(), a, verb, u.getFormData(), null);
		} else if (verb.endsWith("+1")) {
			// needsArgument
			verb = verb.substring(0, verb.length()-2);
			return wf.applyCommand(u.getUser(), a, verb, null, null);
		} else if (wf.hasMatchingCommand(verb, a)) {
			// no argument needed
			return wf.applyCommand(u.getUser(), a, verb, null, null);
		}
		
		return null;
	}

}
