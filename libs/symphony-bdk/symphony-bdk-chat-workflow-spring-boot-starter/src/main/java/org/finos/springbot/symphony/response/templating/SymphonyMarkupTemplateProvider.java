package org.finos.springbot.symphony.response.templating;

import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.response.templating.Markup;
import org.finos.springbot.workflow.response.templating.SimpleMarkupTemplateProvider;
import org.springframework.core.io.ResourceLoader;

public class SymphonyMarkupTemplateProvider extends SimpleMarkupTemplateProvider {

	public SymphonyMarkupTemplateProvider(String templatePrefix, String templateSuffix, ResourceLoader rl,
			BiFunction<Content, Markup, String> converter) {
		super(templatePrefix, templateSuffix, rl, converter);
	}

	/**
	 * When markup is being inserted into a template, we must remove the
	 * <messageML> at the start/end.
	 */
	@Override
	protected String prepareMarkupForInsertion(String markup) {
		if (markup.startsWith("<messageML>")) {
			markup = markup.substring(11);
		}
		
		if (markup.endsWith("</messageML>")) {
			markup = markup.substring(0, markup.length()-12);
		}
		
		return markup;
	}
	
	

}
