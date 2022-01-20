package org.finos.springbot.workflow.response.templating;

import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.core.io.ResourceLoader;

public class SimpleMarkupTemplateProvider extends AbstractMarkupTemplateProvider<Markup> {

	public SimpleMarkupTemplateProvider(String templatePrefix, String templateSuffix, String defaultTemplateName, ResourceLoader rl,
			BiFunction<Content, Markup, String> converter) {
		super(templatePrefix, templateSuffix, defaultTemplateName, rl, converter);
	}

	@Override
	protected Markup toMarkup(String s, Markup ctx) {
		return Markup.of(s);
	}

	@Override
	protected Markup createContext(MessageResponse t) {
		return Markup.EMPTY_MARKUP;
	}

}
