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
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ErrorHandler;


public class TableEditRow extends AbstractTableActionConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();

	public static final String EDIT_SUFFIX = "table-edit-row";
	
	public static final String UPDATE_SUFFIX = "table-update";

	public static String fixSpel(String in) {
		return in.replace("entity.formdata.", "").replace(".[", "[");
	}
	
	public TableEditRow(ErrorHandler errorHandler, ResponseHandlers rh) {
		super(errorHandler, rh);
	}
	
	@Override
	public void acceptFormAction(FormAction in) {
		String verb = in.getAction();
		if (verb == null) {
			return;
		}
			
		if (verb.endsWith(EDIT_SUFFIX)) {
			createEditForm(in, verb);
		} else if (verb.endsWith(UPDATE_SUFFIX)) {
			updateData(in, verb);
		}
	}

	protected void updateData(FormAction in, String verb) {
		String tableLocation = verb.substring(0, verb.length() - UPDATE_SUFFIX.length()-1);
		tableLocation = fixSpel(tableLocation);
		int lastBracket = tableLocation.lastIndexOf('[');
		int row = Integer.parseInt(tableLocation.substring(lastBracket+1, tableLocation.length()-1));
		tableLocation = tableLocation.substring(0, lastBracket);
		Object updated = in.getFormData();
		Expression e = spel.parseExpression(tableLocation);
		Object data = in.getData().get(WORKFLOW_001);
		@SuppressWarnings("unchecked")
		List<Object> listToUpdate = (List<Object>) e.getValue(data);
		listToUpdate.set(row, updated);
		
		WorkResponse wr = new WorkResponse(
				in.getAddressable(),
				data,
				WorkMode.EDIT);
		
		rh.accept(wr);
	}

	protected void createEditForm(FormAction in, String verb) {
		String tableLocation = verb.substring(0, verb.length() - EDIT_SUFFIX.length()-1);
		tableLocation = fixSpel(tableLocation);
		Expression e = spel.parseExpression(tableLocation);
		Object data = in.getData().get(WorkResponse.OBJECT_KEY);
		Object o = e.getValue(data);
		Class<?> c = o.getClass();
		
		Map<String, Object> json = WorkResponse.createEntityMap(o, 
				ButtonList.of(new Button(tableLocation+"."+UPDATE_SUFFIX, Type.ACTION, "Update")), 
				null);

		json.put(WORKFLOW_001, data);
		
		WorkResponse wr = new WorkResponse(
				in.getAddressable(),
				json,
				WorkResponse.getTemplateNameForClass(WorkMode.EDIT, c),
				WorkMode.EDIT,
				c);
		
		
		rh.accept(wr);
	}

	
	
}
