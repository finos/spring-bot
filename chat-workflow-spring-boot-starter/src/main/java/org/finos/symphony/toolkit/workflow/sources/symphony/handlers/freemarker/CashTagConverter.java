package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.springframework.stereotype.Component;

@Component
public class CashTagConverter extends AbstractClassFieldConverter {

	public CashTagConverter() {
		super(LOW_PRIORITY, HashTag.class);
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField context) {
		if (editMode) {
			return textField(variable);
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><cash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".name!''")
				+ " /></#if>";
		}
	}

}
