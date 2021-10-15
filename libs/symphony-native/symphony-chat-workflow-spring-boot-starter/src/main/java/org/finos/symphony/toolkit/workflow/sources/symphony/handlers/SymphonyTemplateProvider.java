package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter.Mode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

public class SymphonyTemplateProvider extends AbstractResourceTemplateProvider<String, WorkResponse> {

	private final FormMessageMLConverter formConverter;
	
	public SymphonyTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			ResourceLoader rl, 
			FormMessageMLConverter formConverter
		) {
		super(templatePrefix, templateSuffix, rl);
		this.formConverter = formConverter;
	}

	@Override
	protected String getDefaultTemplate(WorkResponse r) {
		String insert;
		if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			insert = formConverter.convert(c, Mode.FORM);
		} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			boolean needsButtons = needsButtons(r);						
			insert = formConverter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		} else {
			throw new UnsupportedOperationException("Don't know how to construct default template for "+r);
		}
		
		return insert;
	}
	
	protected boolean needsButtons(Response r) {
		if (r instanceof WorkResponse) {
			ButtonList bl = (ButtonList) ((WorkResponse) r).getData().get(ButtonList.KEY);
			return (bl != null) && (bl.getContents().size() > 0);
		} else {
			return false;
		}
	}

	@Override
	protected String deserializeTemplate(InputStream is) throws IOException {
		return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
	}

	@Override
	protected String applyTemplate(String template, WorkResponse t) {
		return template;
	}

}
