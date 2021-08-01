package org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.AbstractElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;


public class TableEditRow extends AbstractElementsConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();

	public static final String EDIT_SUFFIX = "table-edit-row";
	
	public static final String UPDATE_SUFFIX = "table-update";

	public static String fixSpel(String in) {
		return in.replace("entity.formdata.", "").replace(".[", "[");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Response> apply(ElementsAction in) {
		Workflow wf = in.getWorkflow();
		String verb = in.getAction();
		if (verb == null) {
			return null;
		}
		
		EntityJson ej = in.getData();
		Object data = ej.get(EntityJsonConverter.WORKFLOW_001);
		
		if (verb.endsWith(EDIT_SUFFIX)) {
			String tableLocation = verb.substring(0, verb.length() - EDIT_SUFFIX.length()-1);
			tableLocation = fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			Object o = e.getValue(data);
			Class<?> c = o.getClass();
			return Collections.singletonList(new FormResponse(wf, in.getAddressable(), ej, "Edit "+wf.getName(c), "Update Row Details", o, true, ButtonList.of(new Button(tableLocation+"."+UPDATE_SUFFIX, Type.ACTION, "Update"))));
		} else if (verb.endsWith(UPDATE_SUFFIX)) {
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
			return Collections.singletonList(new FormResponse(wf, in.getAddressable(), ej, wf.getName(c), wf.getInstructions(c), data, false, wf.gatherButtons(data, in.getAddressable())));
		}
		
		return null;
	}

	
	
}
