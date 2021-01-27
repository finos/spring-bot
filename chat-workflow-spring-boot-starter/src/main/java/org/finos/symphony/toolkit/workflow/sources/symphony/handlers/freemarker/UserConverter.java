package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter extends AbstractClassFieldConverter {

	public UserConverter() {
		super(LOW_PRIORITY, User.class);
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField context) {
		if (editMode) {
			return formatErrorsAndIndent(variable) 
					+ "<person-selector " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName())
					+" required=\"false\"/>";
		} else {
			return "<#if " + variable.getDataPath() +"??><mention "
					+ attributeParam(variable, "uid", variable.field("id").getDataPath())
					+ " /></#if>";
		}
	}

}
