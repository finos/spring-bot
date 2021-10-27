package org.finos.springbot.symphony.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.symphony.content.CashTag;
import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

public class CashTagConverter extends AbstractClassConverter<String> {

	public CashTagConverter(Rendering<String> r) {
		super(LOW_PRIORITY, r, CashTag.class);
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
