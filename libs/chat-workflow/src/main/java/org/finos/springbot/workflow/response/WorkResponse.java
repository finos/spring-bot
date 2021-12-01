package org.finos.springbot.workflow.response;

import java.util.HashMap;
import java.util.Map;

import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.ErrorMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

/**
 * Returns @Work-annotated object back to the user, either as form if editable=true, or 
 * displayed in the chat if editable=false.
 * 
 * @author rob@kite9.com
 *
 */
public class WorkResponse extends DataResponse {
	
	public static final String DEFAULT_FORM_TEMPLATE_EDIT = "default-edit";
	public static final String DEFAULT_FORM_TEMPLATE_VIEW = "default-view";
	public static final String ERRORS_KEY = "errors";
	public static final String OBJECT_KEY = "form";
	
	private final WorkMode mode;
	private final Class<?> formClass;
	
	public WorkResponse(Addressable to, Map<String, Object> data, String templateName, WorkMode m, Class<?> formClass) {
		super(to, data, templateName);
		this.mode = m;
		this.formClass = formClass;
	}
	
	/**
	 * Call this contructor to create a basic form response using an object.
	 */
	public WorkResponse(Addressable to, Object o, WorkMode m, ButtonList buttons, ErrorMap errors) {
		this(to, createEntityMap(o, buttons, errors), getTemplateNameForObject(m, o), m, o.getClass());
	}
	
	public WorkResponse(Addressable to, Object o, WorkMode m) {
		this(to, o, m, null, null);
	}
	
	public static Map<String, Object> createEntityMap(Object o, ButtonList buttons, ErrorMap errors) {
		Map<String, Object> json = new HashMap<>();
		json.put(ButtonList.KEY, buttons == null ? new ButtonList() : buttons);
		json.put(ERRORS_KEY, errors == null ? new ErrorMap() : errors);
		json.put(OBJECT_KEY, o);
		return json;
	}

	public static String getTemplateNameForObject(WorkMode m, Object o) {
		return getTemplateNameForClass(m, o.getClass());
	}
	
	public static String getTemplateNameForClass(WorkMode m, Class<?> c) {
		Template t = c.getAnnotation(Template.class);
		String templateName = t == null ? null : (m == WorkMode.EDIT ? t.edit() : t.view());
		
		if (!StringUtils.hasText(templateName)) {
			return (m == WorkMode.EDIT) ? DEFAULT_FORM_TEMPLATE_EDIT : DEFAULT_FORM_TEMPLATE_VIEW;
		} else {
			return templateName;
		}
		
	}

	@Override
	public Map<String, Object> getData() {
		return (Map<String, Object>) super.getData();
	}

	public Object getFormObject() {
		return getData().get(OBJECT_KEY);
	}
	
	public Class<?> getFormClass() {
		return formClass;
	}

	public ButtonList getButtons() {
		return (ButtonList) getData().get(ButtonList.KEY);
	}

	public Errors getErrors() {
		return (Errors) getData().get(ERRORS_KEY);
	}

	@Override
	public String toString() {
		return "WorkResponse [getData()=" + getData() + ", getTemplateName()="
				+ getTemplateName() + ", getAddress()=" + getAddress() + "]";
	}
	
	public WorkMode getMode() {
		return mode;
	}

	
}
