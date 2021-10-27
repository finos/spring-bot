package org.finos.springbot.symphony.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

public class HashTagConverter extends AbstractClassConverter<String> {

	public HashTagConverter(Rendering<String> r) {
		super(LOW_PRIORITY, r, HashTag.class);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return textField(variable.field("value"), variable.getFormFieldName(), variable.getDisplayName());
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><hash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".value!''")
				+ " /></#if>";
		}
	}

}
