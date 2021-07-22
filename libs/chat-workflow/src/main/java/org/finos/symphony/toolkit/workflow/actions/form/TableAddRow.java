package org.finos.symphony.toolkit.workflow.actions.form;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
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
		String verb = ea.getAction();
		if (verb == null) {
			return;
		}
		
		if (verb.endsWith(ACTION_SUFFIX)) {
			newRowFormAction(ea, verb);
		} else if (verb.endsWith(DO_SUFFIX)) {
			addNewRowAction(ea, verb);
		} 
	}


	protected void addNewRowAction(FormAction ea, String verb) {
		String tableLocation = verb.substring(0, verb.length() - DO_SUFFIX.length()-1);
		tableLocation = TableEditRow.fixSpel(tableLocation);
		Expression e = spel.parseExpression(tableLocation);
		Object updated = ea.getFormData();
		List<Object> listToUpdate = (List<Object>) e.getValue(workflowObject);
		listToUpdate.add(updated);
	}


	protected void newRowFormAction(FormAction ea, String verb) {
		Map<String, Object> ej = ea.getData();
		Object workflowObject = ej.get(WorkResponse.OBJECT_KEY);

		String tableLocation = verb.substring(0, verb.length() - ACTION_SUFFIX.length()-1);
		tableLocation = TableEditRow.fixSpel(tableLocation);
		Expression e = spel.parseExpression(tableLocation);
		TypeDescriptor td  = e.getValueTypeDescriptor(workflowObject);
		Class<?> c = td.getResolvableType().getGeneric(0).resolve();
		
		Object out = null;
		try {
			out = c.getConstructor().newInstance();
		} catch (Exception e1) {
			errorHandler.handleError(e1);
		}
		
		Map<String, Object> data = WorkResponse.createEntityJson(out, 
				ButtonList.of(new Button(tableLocation+"."+DO_SUFFIX, Type.ACTION, "Add"), null), 
				null);
		
		// keep track of the original data
		data.put(WORKFLOW_001, workflowObject);
		
		
		WorkResponse wr = new WorkResponse(
				ea.getAddressable(),
				data,
				WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT,
				WorkMode.EDIT);
		
		rh.accept(wr);
	}
	
}
