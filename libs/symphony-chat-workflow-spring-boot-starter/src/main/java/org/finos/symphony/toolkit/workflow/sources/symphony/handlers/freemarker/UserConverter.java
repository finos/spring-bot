package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.workflow.content.User;

public class UserConverter extends AbstractClassConverter {

	public UserConverter() {
		super(LOW_PRIORITY, User.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
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
