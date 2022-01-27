package org.finos.springbot.symphony.content.serialization;

import java.util.Map;
import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.finos.springbot.workflow.response.templating.Markup;

public class SymphonyMarkupWriter extends MarkupWriter<Markup>{

	public SymphonyMarkupWriter() {
		super();
	}

	public SymphonyMarkupWriter(
			Map<Class<? extends Content>, BiFunction<Content, Markup, String>> tagMap) {
		super(tagMap);
	}

	
}
