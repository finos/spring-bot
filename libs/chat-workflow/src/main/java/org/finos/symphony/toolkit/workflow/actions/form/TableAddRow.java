package org.finos.symphony.toolkit.workflow.actions.form;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ErrorHandler;



public class TableAddRow extends AbstractTableActionConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();
	

	public static final String ACTION_SUFFIX = "table-add-row";
	public static final String DO_SUFFIX = "table-add-done";
	
	public TableAddRow(ErrorHandler errorHandler, ResponseHandlers rh) {
		super(errorHandler, rh);
	}


	@Override
	public void acceptFormAction(FormAction ea) {
		Workflow wf = ea.getWorkflow();
		String verb = ea.getAction();
		if (verb == null) {
			return null;
		}
		EntityJson ej = ea.getData();
		Object workflowObject = ej.get(EntityJsonConverter.WORKFLOW_001);
		
		if (verb.endsWith(ACTION_SUFFIX)) {
			String tableLocation = verb.substring(0, verb.length() - ACTION_SUFFIX.length()-1);
			tableLocation = TableEditRow.fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			TypeDescriptor td  = e.getValueTypeDescriptor(workflowObject);
			Class<?> c = td.getResolvableType().getGeneric(0).resolve();
			
			Object out;
			try {
				out = c.newInstance(); 
			} catch (Exception e1) {
				return Collections.singletonList(new WorkResponse(wf, ea.getAddressable(), ej, "New "+wf.getName(c), "Provide details for the new row", c, true, ButtonList.of(new Button(tableLocation+"."+DO_SUFFIX, Type.ACTION, "Add"))));
			}
			return Collections.singletonList(new WorkResponse(wf, ea.getAddressable(), ej, "New "+wf.getName(c), "Provide details for the new row", out, true, ButtonList.of(new Button(tableLocation+"."+DO_SUFFIX, Type.ACTION, "Add"))));
		} else if (verb.endsWith(DO_SUFFIX)) {
			String tableLocation = verb.substring(0, verb.length() - DO_SUFFIX.length()-1);
			tableLocation = TableEditRow.fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			Object updated = ea.getFormData();
			List<Object> listToUpdate = (List<Object>) e.getValue(workflowObject);
			listToUpdate.add(updated);
			return Collections.singletonList(
				new WorkResponse(wf, ea.getAddressable(), 
						ej, 
						wf.getName(workflowObject.getClass()), 
						wf.getInstructions(workflowObject.getClass()), 
						workflowObject, false, 
						wf.gatherButtons(workflowObject, ea.getAddressable())));
		} 
		
		return null;
	}
	
}
