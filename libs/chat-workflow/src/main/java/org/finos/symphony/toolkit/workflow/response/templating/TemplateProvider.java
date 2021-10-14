package org.finos.symphony.toolkit.workflow.response.templating;

import org.finos.symphony.toolkit.workflow.response.Response;

public interface TemplateProvider<T, V extends Response> {

	public T template(V dr);
}
