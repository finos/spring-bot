package org.finos.springbot.workflow.response.templating;

import java.io.IOException;
import java.io.InputStream;

import org.finos.springbot.workflow.response.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

public abstract class AbstractResourceTemplateProvider<T, V extends DataResponse> implements TemplateProvider<T, V> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceTemplateProvider.class);

	protected final String templatePrefix;
	protected final String templateSuffix;
	protected final ResourceLoader rl;

	public AbstractResourceTemplateProvider(String templatePrefix, String templateSuffix, ResourceLoader rl) {
		super();
		this.templatePrefix = templatePrefix;
		this.templateSuffix = templateSuffix;
		this.rl = rl;
	}
	
	public T template(V t) {
		String templateName = t.getTemplateName();

		T template = StringUtils.hasText(templateName) ? getTemplateForName(templateName) : null;

		if (template == null) {
			LOG.info("Reverting to default template for " + t);
			template = getDefaultTemplate(t);
			LOG.info("Template: \n"+template);
		}
		
		T out = applyTemplate(template, t);
		
		return out;
	}

	protected abstract T applyTemplate(T template, V t);

	public T getTemplateForName(String name) {
		try {
			return resolveTemplate(name);
		} catch (Exception e) {
			LOG.debug("Couldn't find template: "+name);
			return null;
		}
	}

	protected InputStream loadTemplateStream(String name) throws IOException {
		return rl.getResource(templatePrefix + name + templateSuffix).getInputStream();
	}
	
	/**
	 * Return the template for the given name
	 */
	protected T resolveTemplate(String name) throws Exception {
		return deserializeTemplate(loadTemplateStream(name));
	}
	
	/**
	 * Turns a stream into a template object
	 * @throws Exception 
	 */
	protected abstract T deserializeTemplate(InputStream is) throws Exception;
	
	/**
	 * Where template name is not provided in the {@link DataResponse}, construct
	 * and return a default template.
	 */
	protected abstract T getDefaultTemplate(V r);

}
