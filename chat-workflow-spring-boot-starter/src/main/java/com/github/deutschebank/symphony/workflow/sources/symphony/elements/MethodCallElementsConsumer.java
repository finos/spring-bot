package com.github.deutschebank.symphony.workflow.sources.symphony.elements;

import java.util.Collections;
import java.util.List;

import com.github.deutschebank.symphony.workflow.CommandPerformer;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.response.Response;

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
