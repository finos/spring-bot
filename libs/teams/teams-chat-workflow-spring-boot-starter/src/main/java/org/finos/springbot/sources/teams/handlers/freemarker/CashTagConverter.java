package org.finos.springbot.sources.teams.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.sources.teams.content.CashTag;

public class CashTagConverter extends AbstractClassConverter {

	public CashTagConverter() {
		super(LOW_PRIORITY, CashTag.class);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return textField(variable.field("id[0].value"), variable.getFormFieldName(), variable.getDisplayName());
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><cash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".id[0].value!''")
				+ " /></#if>";
		}
	}

}
