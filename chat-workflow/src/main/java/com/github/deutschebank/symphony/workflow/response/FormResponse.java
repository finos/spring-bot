package com.github.deutschebank.symphony.workflow.response;

import org.springframework.validation.Errors;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.form.ButtonList;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.validation.ErrorHelp;

public class FormResponse extends DataResponse {

	private final boolean editable;
	private final Object formObject;
	private final Class<?> formClass;
	private final ButtonList buttons;
	private final Errors errors;
		
	public FormResponse(Workflow wf, Addressable stream, Object data, String name, String instructions, Object formObject, boolean editable, ButtonList buttons, Errors e) {
		super(wf, stream, EntityJsonConverter.newWorkflow(data), name, instructions);
		this.editable = editable;
		this.formObject = formObject;
		this.buttons = buttons;
		this.formClass = formObject.getClass();
		this.errors = e;
	}

	public FormResponse(Workflow wf, Addressable stream, Object data, String name, String instructions, Object formObject, boolean editable, ButtonList buttons) {
		this(wf, stream, data, name, instructions, formObject, editable, buttons, ErrorHelp.createErrorHolder());
	}
	
	public FormResponse(Workflow wf, Addressable stream, Object data, String name, String instructions, Class<?> formClass, boolean editable, ButtonList buttons) {
		super(wf, stream, EntityJsonConverter.newWorkflow(data), name, instructions);
		this.editable = editable;
		this.formObject = null;
		this.buttons = buttons;
		this.formClass = formClass;
		this.errors = ErrorHelp.createErrorHolder();
	}

	public boolean isEditable() {
		return editable;
	}

	public Object getFormObject() {
		return formObject;
	}

	public ButtonList getButtons() {
		return buttons;
	}
	
	public Class<?> getFormClass() {
		return formClass;
	}

	public Errors getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "FormResponse [editable=" + editable + ", formObject=" + formObject + ", formClass=" + formClass
				+ ", buttons=" + buttons + ", errors=" + errors + "]";
	}


}
