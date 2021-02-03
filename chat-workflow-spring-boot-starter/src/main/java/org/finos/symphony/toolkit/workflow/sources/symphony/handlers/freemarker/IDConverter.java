package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.ID;
import org.springframework.stereotype.Component;

@Component
public class IDConverter extends AbstractClassConverter {

	public IDConverter() {
		super(MED_PRIORITY, ID.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable, EntityJson ej) {
		if (editMode) {
			return "";
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><hash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".name!''")
				+ " /></#if>";
		}
	}

}
