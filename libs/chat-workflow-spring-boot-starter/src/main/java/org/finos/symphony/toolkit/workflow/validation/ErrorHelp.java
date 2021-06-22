package org.finos.symphony.toolkit.workflow.validation;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;


@Component
public class ErrorHelp {

	public static Errors createErrorHolder() {
		return new MapBindingResult(new HashMap<>(), "");
	}

	public static String errors(Errors e) {
		return e.getFieldErrors(null).stream()
			.map(m -> {
				return "<span class=\"tempo-text-color--red\">"+m.getDefaultMessage()+"</span>";
			})
			.reduce("", String::concat);
	}

	@Autowired
	Validator validator;

	public Errors performErrorHandling(Object currentForm) {
		if (currentForm == null) {
			return null;
		}
		
		Errors errors = ErrorHelp.createErrorHolder();
		validator.validate(currentForm, errors);
		return errors;
	}
		

}
