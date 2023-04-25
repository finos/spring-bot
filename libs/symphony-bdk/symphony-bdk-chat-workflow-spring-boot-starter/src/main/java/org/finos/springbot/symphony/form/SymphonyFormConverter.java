package org.finos.springbot.symphony.form;

import java.util.Map;

import org.finos.springbot.workflow.form.FormConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SymphonyFormConverter extends FormConverter {

	public SymphonyFormConverter(ObjectMapper om) {
		super(om);
	}

	@Override
	public Object convert(Map<String, Object> formValues, String type) throws ClassNotFoundException {
		if (formValues.containsKey("entity.formdata")) {
			try {
				// nosemgrep
				Class<?> c = Class.forName(type);
				return om.convertValue(formValues.get("entity.formdata"), c);
			} catch (Exception e1) {
				LOG.debug("Couldn't convert {} ",formValues, e1);
			}
		}
		
		return super.convert(formValues, type);
	}
}
