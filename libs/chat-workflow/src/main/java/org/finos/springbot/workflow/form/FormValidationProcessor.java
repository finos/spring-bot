package org.finos.springbot.workflow.form;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

/**
 * This encapsulates server-side validation logic needed when forms are received.
 * 
 * @author rob@kite9.com
 *
 */
public class FormValidationProcessor {

	private Validator v;
	private ResponseHandlers rh;
	
	public FormValidationProcessor(Validator v, ResponseHandlers rh) {
		super();
		this.v = v;
		this.rh = rh;
	}

	public FormAction validationCheck(String verb, Addressable from, Object form, Supplier<FormAction> callback) {
		Errors e = ErrorHelp.createErrorHolder();
		
		if (validated(form, e)) {
			return callback.get();
		} else {
			WorkResponse fr = new WorkResponse(from, form,  WorkMode.EDIT, 
				ButtonList.of(new Button(verb, Button.Type.ACTION, "Retry")), convertErrorsToMap(e));
			rh.accept(fr);
			return null;
		}
	}

	protected boolean validated(Object currentForm, Errors e) {
		if ((currentForm != null) && (!(currentForm instanceof FormSubmission))) {
			v.validate(currentForm, e);
			return !e.hasErrors();
		} else {
			return true;
		}
	}

	public static ErrorMap convertErrorsToMap(Errors e) {
		return e == null ? new ErrorMap() : new ErrorMap(e.getAllErrors().stream()
			.map(err -> (FieldError) err)
			.collect(Collectors.toMap(fe -> fe.getField(), fe -> ""+fe.getDefaultMessage())));
	}


}

