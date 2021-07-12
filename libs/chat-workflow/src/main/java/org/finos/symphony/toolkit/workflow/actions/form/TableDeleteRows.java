package org.finos.symphony.toolkit.workflow.actions.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.form.FormSubmission;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ErrorHandler;

public class TableDeleteRows extends AbstractTableActionConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();

	public static final String ACTION_SUFFIX = "table-delete-rows";
	public static final String SELECT_SUFFIX = "selected";

	public TableDeleteRows(ErrorHandler errorHandler, ResponseHandlers rh) {
		super(errorHandler, rh);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void acceptFormAction(FormAction ea) {
		Workflow wf = ea.getWorkflow();
		String verb = ea.getAction();
		if (verb == null) {
			return null;
		}
		
		EntityJson ej = ea.getData();
		Object data = ej.get(EntityJsonConverter.WORKFLOW_001);
		
		if (verb.endsWith(ACTION_SUFFIX)) {
			
			// get the table to modify
			String tableLocation = verb.substring(0, verb.length() - ACTION_SUFFIX.length()-1);
			tableLocation = TableEditRow.fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			List<Object> table = (List<Object>) e.getValue(data);
			Object deleteStructure = ((FormSubmission)ea.getFormData()).structure;
			
			String mapLocation = convertSpelToMapSpel(tableLocation);
			e = spel.parseExpression(mapLocation);
			List<Integer> toRemove = getRowsToDelete((List<Object>) e.getValue(deleteStructure));
			
			for (Integer i : toRemove) {
				table.remove((int) i);
			}
			
			return Collections.singletonList(
				new FormResponse(wf, ea.getAddressable(), ej, 
						wf.getName(data.getClass()), 
						wf.getInstructions(data.getClass()), 
						data, false, 
						wf.gatherButtons(data, ea.getAddressable())));
		}
		
		return null;
	}

	private String convertSpelToMapSpel(String tableLocation) {
		return String.join(".", Arrays.stream(tableLocation.split("\\."))
			.map(i -> i.startsWith("[") ? i : "['" + i + "']")
			.toArray(i -> new String[i]));
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getRowsToDelete(List<Object> value) {
		return IntStream.range(0, value.size())
			.filter(i -> {
				Object val = value.get(i);
				return (val != null) && ((Map<String, Object>) val).containsKey("selected");
			})
			.mapToObj(i -> i)
			.sorted(Collections.reverseOrder())
			.collect(Collectors.toList());
	}
}
