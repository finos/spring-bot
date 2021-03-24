package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.HashTag;

public class HashTagConverter extends AbstractClassConverter {

	public HashTagConverter() {
		super(LOW_PRIORITY, HashTag.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable, EntityJson ej) {
		if (editMode) {
			return textField(variable);
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><hash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".name!''")
				+ " /></#if>";
		}
	}

}
