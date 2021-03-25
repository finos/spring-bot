package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.springframework.validation.Errors;

public interface FormMessageMLConverter {

	String convert(Class<?> c, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work);

}