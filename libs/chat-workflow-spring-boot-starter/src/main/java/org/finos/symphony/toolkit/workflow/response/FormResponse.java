package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.actions.Template;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.springframework.validation.Errors;

public class FormResponse extends DataResponse {
	
	public static final String BUTTONLIST_KEY = "buttons";
	public static final String ERRORS_KEY = "errors";
	public static final String FORMOBJECT_KEY = "form";
	
	private final boolean editable;
			
	public FormResponse(Addressable to, EntityJson data, String templateName, boolean editable) {
		super(to, data, templateName);
		this.editable = editable;
	}
	
	/**
	 * Call this contructor to create a basic form response using an object.
	 */
	public FormResponse(Addressable to, Object o, boolean editable, ButtonList buttons, Errors errors) {
		this(to, createEntityJson(o, buttons, errors), getTemplateNameForObject(editable, o), editable);
	}
	
	public static EntityJson createEntityJson(Object o, ButtonList buttons, Errors errors) {
		EntityJson json = new EntityJson();
		json.put(BUTTONLIST_KEY, buttons);
		json.put(ERRORS_KEY, errors);
		json.put(FORMOBJECT_KEY, o);
		return json;
	}

	public static String getTemplateNameForObject(boolean editable, Object o) {
		return getTemplateNameForClass(editable, o.getClass());
	}
	
	public static String getTemplateNameForClass(boolean editMode, Class<?> c) {
		Template t = c.getAnnotation(Template.class);
		String templateName = t == null ? null : (editMode ? t.edit() : t.view());
		return templateName;
	}

	public boolean isEditable() {
		return editable;
	}

	public Object getFormObject() {
		return getData().get(FORMOBJECT_KEY);
	}

	public ButtonList getButtons() {
		return (ButtonList) getData().get(BUTTONLIST_KEY);
	}

	public Errors getErrors() {
		return (Errors) getData().get(ERRORS_KEY);
	}

	@Override
	public String toString() {
		return "FormResponse [editable=" + editable + ", getData()=" + getData() + ", getTemplateName()="
				+ getTemplateName() + ", getAddress()=" + getAddress() + "]";
	}

	
}
