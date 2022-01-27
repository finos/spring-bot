package org.finos.springbot.teams.response.templating;

import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.templating.AbstractMarkupTemplateProvider;
import org.springframework.core.io.ResourceLoader;

public class EntityMarkupTemplateProvider extends AbstractMarkupTemplateProvider<MarkupAndEntities>  {

	public EntityMarkupTemplateProvider(String templatePrefix, String templateSuffix, String defaultTemplateName, ResourceLoader rl,
			BiFunction<Content, MarkupAndEntities, String> converter) {
		super(templatePrefix, templateSuffix, defaultTemplateName, rl, converter);
	}

	@Override
	protected MarkupAndEntities toMarkup(String s, MarkupAndEntities ctx) {
		return new MarkupAndEntities(s, ctx.getEntities());
	}

	@Override
	protected MarkupAndEntities createContext(MessageResponse t) {
		return new MarkupAndEntities();
	}

}
