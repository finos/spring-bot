package org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.actions.ElementsAction;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.AbstractElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;



public class TableAddRow extends AbstractElementsConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();
	

	public static final String ACTION_SUFFIX = "table-add-row";
	public static final String DO_SUFFIX = "table-add-done";


	@Override
	public List<Response> apply(ElementsAction ea) {
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
				return Collections.singletonList(new FormResponse(wf, ea.getAddressable(), ej, "New "+wf.getName(c), "Provide details for the new row", c, true, ButtonList.of(new Button(tableLocation+"."+DO_SUFFIX, Type.ACTION, "Add"))));
			}
			return Collections.singletonList(new FormResponse(wf, ea.getAddressable(), ej, "New "+wf.getName(c), "Provide details for the new row", out, true, ButtonList.of(new Button(tableLocation+"."+DO_SUFFIX, Type.ACTION, "Add"))));
		} else if (verb.endsWith(DO_SUFFIX)) {
			String tableLocation = verb.substring(0, verb.length() - DO_SUFFIX.length()-1);
			tableLocation = TableEditRow.fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			Object updated = ea.getFormData();
			List<Object> listToUpdate = (List<Object>) e.getValue(workflowObject);
			listToUpdate.add(updated);
			return Collections.singletonList(
				new FormResponse(wf, ea.getAddressable(), 
						ej, 
						wf.getName(workflowObject.getClass()), 
						wf.getInstructions(workflowObject.getClass()), 
						workflowObject, false, 
						wf.gatherButtons(workflowObject, ea.getAddressable())));
		} 
		
		return null;
	}
	
}
