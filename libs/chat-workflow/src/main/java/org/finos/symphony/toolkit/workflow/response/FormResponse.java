package org.finos.symphony.toolkit.workflow.response;

import java.util.HashMap;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.annotations.Template;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.ErrorMap;
import org.springframework.validation.Errors;

public class FormResponse extends DataResponse {
	
	public static final String DEFAULT_FORM_TEMPLATE_EDIT = "default-edit";
	public static final String DEFAULT_FORM_TEMPLATE_VIEW = "default-view";
	public static final String BUTTONLIST_KEY = "buttons";
	public static final String ERRORS_KEY = "errors";
	public static final String FORMOBJECT_KEY = "form";
				
	public FormResponse(Addressable to, Map<String, Object> data, String templateName) {
		super(to, data, templateName);
	}
	
	/**
	 * Call this contructor to create a basic form response using an object.
	 */
	public FormResponse(Addressable to, Object o, boolean editable, ButtonList buttons, ErrorMap errors) {
		this(to, createEntityJson(o, buttons, errors), getTemplateNameForObject(editable, o));
	}
	
	public FormResponse(Addressable to, Object o, boolean editable) {
		this(to, o, editable, null, null);
	}
	
	public static Map<String, Object> createEntityJson(Object o, ButtonList buttons, ErrorMap errors) {
		Map<String, Object> json = new HashMap<>();
		json.put(BUTTONLIST_KEY, buttons == null ? new ButtonList() : buttons);
		json.put(ERRORS_KEY, errors == null ? new ErrorMap() : errors);
		json.put(FORMOBJECT_KEY, o);
		return json;
	}

	public static String getTemplateNameForObject(boolean editable, Object o) {
		return getTemplateNameForClass(editable, o.getClass());
	}
	
	public static String getTemplateNameForClass(boolean editMode, Class<?> c) {
		Template t = c.getAnnotation(Template.class);
		String templateName = t == null ? null : (editMode ? t.edit() : t.view());
		
		if (templateName == null) {
			return editMode ? DEFAULT_FORM_TEMPLATE_EDIT : DEFAULT_FORM_TEMPLATE_VIEW;
		} else {
			return templateName;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getData() {
		return (Map<String, Object>) super.getData();
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
		return "FormResponse [getData()=" + getData() + ", getTemplateName()="
				+ getTemplateName() + ", getAddress()=" + getAddress() + "]";
	}

	
}
