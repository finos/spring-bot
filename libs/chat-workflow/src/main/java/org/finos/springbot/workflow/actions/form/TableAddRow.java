package org.finos.springbot.workflow.actions.form;

import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
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


	// nosemgrep
	@SuppressWarnings("unchecked")
	protected void addNewRowAction(FormAction ea, String verb) {
		String tableLocation = verb.substring(0, verb.length() - DO_SUFFIX.length()-1);
		tableLocation = TableEditRow.fixSpel(tableLocation);
		Expression e = spel.parseExpression(tableLocation);
		Object updated = ea.getFormData();
		Object toChange = ea.getData().get(WORKFLOW_001);
		List<Object> listToUpdate = (List<Object>) e.getValue(toChange);
		listToUpdate.add(updated);
		
		WorkResponse wr = new WorkResponse(
				ea.getAddressable(),
				toChange,
				WorkMode.EDIT);
		
		rh.accept(wr);
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
			// this is called for primitives where we can't construct the new instance.
		}
		
		Map<String, Object> data = WorkResponse.createEntityMap(out, 
				ButtonList.of(new Button(tableLocation+"."+DO_SUFFIX, Type.ACTION, "Add")), 
				null);
		
		// keep track of the original data
		data.put(WORKFLOW_001, workflowObject);
		
		
		WorkResponse wr = new WorkResponse(
				ea.getAddressable(),
				data,
				WorkResponse.getTemplateNameForClass(WorkMode.EDIT, c),
				WorkMode.EDIT,
				c);
		
		rh.accept(wr);
	}
	
}
