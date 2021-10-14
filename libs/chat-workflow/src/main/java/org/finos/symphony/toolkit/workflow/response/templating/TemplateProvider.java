package org.finos.symphony.toolkit.workflow.response.templating;

import org.finos.symphony.toolkit.workflow.response.DataResponse;

public interface TemplateProvider<T> {

	public T getTemplate(DataResponse dr);
}
