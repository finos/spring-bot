package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.workflow.content.Author;

public class AuthorConverter extends AbstractClassConverter {

	public AuthorConverter() {
		super(MED_PRIORITY, Author.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
		return "<#if " + variable.getDataPath() +"??><mention "
					+ attributeParam(variable, "uid", variable.field("id").getDataPath())
					+ " /></#if>";
	}

}
