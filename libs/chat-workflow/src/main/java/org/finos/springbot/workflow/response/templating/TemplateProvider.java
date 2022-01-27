package org.finos.springbot.workflow.response.templating;

import org.finos.springbot.workflow.response.Response;

public interface TemplateProvider<T, V extends Response> {

	public T template(V dr);
	
	public boolean hasTemplate(V dr);
}
