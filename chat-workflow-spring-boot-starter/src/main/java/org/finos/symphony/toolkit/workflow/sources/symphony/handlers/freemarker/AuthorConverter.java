package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Author;

public class AuthorConverter extends AbstractClassFieldConverter {

	public AuthorConverter() {
		super(MED_PRIORITY, Author.class);
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField context) {
		return "<#if " + variable.getDataPath() +"??><mention "
					+ attributeParam(variable, "uid", variable.field("id").getDataPath())
					+ " /></#if>";
	}

}
