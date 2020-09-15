package com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit;

import java.util.Collections;
import java.util.List;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.java.ClassBasedWorkflow;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.AbstractElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsAction;


public class TableEditRow  extends AbstractElementsConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();

	public static final String EDIT_SUFFIX = "table-edit-row";
	
	public static final String UPDATE_SUFFIX = "table-update";

	public static String fixSpel(String in) {
		return in.replace(".[", "[");
	}
	
	@Override
	public List<Response> apply(ElementsAction in) {
		Workflow wf = in.getWorkflow();
		String verb = in.getAction();
		if (verb == null) {
			return null;
		}
		
		if (verb.endsWith(EDIT_SUFFIX)) {
			Object data = in.getWorkflowObject();
			String tableLocation = verb.substring(0, verb.length() - EDIT_SUFFIX.length()-1);
			tableLocation = fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			Object o = e.getValue(data);
			Class<?> c = o.getClass();
			return Collections.singletonList(new FormResponse(wf, in.getRoom(), in.getWorkflowObject(), "Edit "+ClassBasedWorkflow.getName(c), "Update Row Details", o, true, Collections.singletonList(new Button(tableLocation+"."+UPDATE_SUFFIX, Type.ACTION, "Update"))));
		} else if (verb.endsWith(UPDATE_SUFFIX)) {
			Object data = in.getWorkflowObject();
			String tableLocation = verb.substring(0, verb.length() - UPDATE_SUFFIX.length()-1);
			tableLocation = fixSpel(tableLocation);
			int lastBracket = tableLocation.lastIndexOf('[');
			int row = Integer.parseInt(tableLocation.substring(lastBracket+1, tableLocation.length()-1));
			tableLocation = tableLocation.substring(0, lastBracket);
			Object updated = in.getFormData();
			Expression e = spel.parseExpression(tableLocation);
			List<Object> listToUpdate = (List<Object>) e.getValue(data);
			listToUpdate.set(row, updated);
			Class<?> c = data.getClass();
			return Collections.singletonList(new FormResponse(wf, in.getRoom(), data, ClassBasedWorkflow.getName(c), ClassBasedWorkflow.getInstructions(c), data, false, wf.gatherButtons(data, in.getRoom())));
		}
		
		return null;
	}

	
	
}
