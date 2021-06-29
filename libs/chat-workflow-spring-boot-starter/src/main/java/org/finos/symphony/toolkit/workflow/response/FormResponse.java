package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.validation.ErrorHelp;
import org.springframework.validation.Errors;

public class FormResponse extends DataResponse {

	private final boolean editable;
	private final Object formObject;
	private final Class<?> formClass;
	private final ButtonList buttons;
	private final Errors errors;
		
	/**
	 * This is for when users have filled the form wrong, and you want to tell them about validation errors.
	 */
	public FormResponse(Addressable stream, EntityJson data, String template, Object formObject, boolean editable, ButtonList buttons, Errors e) {
		super(stream, data, template);
		this.editable = editable;
		this.formObject = formObject;
		this.buttons = buttons;
		this.formClass = formObject.getClass();
		this.errors = e;
	}

	public FormResponse(Addressable stream, EntityJson data, String template, Object formObject, boolean editable, ButtonList buttons) {
		this(stream, data, template, formObject, editable, buttons, ErrorHelp.createErrorHolder());
	}
	
	/**
	 * This is for the empty form
	 */
	public FormResponse(Addressable stream, EntityJson data, String template, Class<?> formClass, boolean editable, ButtonList buttons) {
		super(stream, data, template);
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
