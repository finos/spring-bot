package org.finos.springbot.teams.templating.thymeleaf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.teams.response.templating.MarkupAndEntities;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.springbot.workflow.templating.Mode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import com.microsoft.bot.schema.Entity;

public class ThymeleafTemplateProvider extends AbstractResourceTemplateProvider<MarkupAndEntities, WorkResponse> {

	private final ThymeleafTemplater converter;
	private final SpringTemplateEngine templateEngine;
	
	public ThymeleafTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			ResourceLoader rl, 
			ThymeleafTemplater converter
		) {
		super(templatePrefix, templateSuffix, rl);
		this.converter = converter;
		this.templateEngine = new SpringTemplateEngine();
		this.templateEngine.setTemplateResolver(new StringTemplateResolver());
	}

	@Override
	protected MarkupAndEntities getDefaultTemplate(WorkResponse r) {
		MarkupAndEntities insert;
		if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			insert = converter.convert(c, Mode.FORM);
		} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			boolean needsButtons = needsButtons(r);						
			insert = converter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		} else {
			throw new UnsupportedOperationException("Don't know how to construct default template for "+r);
		}
		
		MarkupAndEntities defaultTemplate = getTemplateForName("default");
		String replacedText = defaultTemplate.getContents().replace("<!-- Message Content -->", insert.getContents());
		List<Entity> allEntities = new ArrayList<Entity>(insert.getEntities());
		allEntities.addAll(defaultTemplate.getEntities());
		MarkupAndEntities out = new MarkupAndEntities(replacedText, allEntities);
		
		return out;
	}
	
	public static boolean needsButtons(Response r) {
		if (r instanceof WorkResponse) {
			ButtonList bl = (ButtonList) ((WorkResponse) r).getData().get(ButtonList.KEY);
			return (bl != null) && (bl.getContents().size() > 0);
		} else {
			return false;
		}
	}

	@Override
	protected MarkupAndEntities deserializeTemplate(InputStream is) throws IOException {
		String template = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
		return new MarkupAndEntities(template);
	}

	@Override
	protected MarkupAndEntities applyTemplate(MarkupAndEntities template, WorkResponse t) {
		// do thymeleaf rendering here
		Context ctx = new Context();
		ctx.setVariable("entity", t.getData());
		String done = templateEngine.process(new TemplateSpec(template.getContents(), TemplateMode.XML), ctx);
		
		return new MarkupAndEntities(done);
	}
	
}
