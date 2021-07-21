package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

public class HashTagConverter extends AbstractClassConverter {

	public HashTagConverter() {
		super(LOW_PRIORITY, HashTag.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return textField(variable.field("value"));
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><hash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".value!''")
				+ " /></#if>";
		}
	}

}
