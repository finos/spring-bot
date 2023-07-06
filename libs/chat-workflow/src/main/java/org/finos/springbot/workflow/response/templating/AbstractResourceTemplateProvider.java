package org.finos.springbot.workflow.response.templating;

import java.io.IOException;
import java.io.InputStream;

import org.finos.springbot.workflow.response.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

public abstract class AbstractResourceTemplateProvider<T, F, V extends DataResponse> implements TemplateProvider<T, V> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceTemplateProvider.class);

	protected final String templatePrefix;
	protected final String templateSuffix;
	protected final String defaultTemplateName;
	protected final ResourceLoader rl;

	public AbstractResourceTemplateProvider(String templatePrefix, String templateSuffix, String defaultTemplateName, ResourceLoader rl) {
		super();
		this.templatePrefix = templatePrefix;
		this.templateSuffix = templateSuffix;
		this.defaultTemplateName = defaultTemplateName;
		this.rl = rl;
	}
	
	public T template(V t) {
		String templateName = t.getTemplateName();

		F template = StringUtils.hasText(templateName) ? getTemplateForName(templateName) : null;

		if (template == null) {
			LOG.info("Reverting to default template for " + t);
			template = getDefaultTemplate(t);
			LOG.info("Template: \n"+template);
		}
		
		T out = applyTemplate(template, t);
		
		return out;
	}

	protected abstract T applyTemplate(F template, V t);

	public F getTemplateForName(String name) {
		try {
			return resolveTemplate(name);
		} catch (Exception e) {
			LOG.error("Couldn't find template: "+e);
			return null;
		}
	}

	protected InputStream loadTemplateStream(String name) throws IOException {
		return getResource(name).getInputStream();
	}

	private Resource getResource(String name) {
		return rl.getResource(templatePrefix + name + templateSuffix);
	}
	
	@Override
	public boolean hasTemplate(V dr) {
		if (dr.getTemplateName() != null) {
			Resource r = getResource(dr.getTemplateName());
			return r.exists();
		} else {
			return false;
		}
	}

	/**
	 * Return the template for the given name
	 */
	protected F resolveTemplate(String name) throws Exception {
		return deserializeTemplate(loadTemplateStream(name));
	}
	
	/**
	 * Turns a stream into a template object
	 * @throws Exception 
	 */
	protected abstract F deserializeTemplate(InputStream is) throws Exception;
	
	/**
	 * Where template name is not provided in the {@link DataResponse}, construct
	 * and return a default template.
	 */
	protected abstract F getDefaultTemplate(V r);
	
	protected String getDefaultTemplateName() {
		return defaultTemplateName;
	}

}
