package org.finos.springbot.workflow.response.templating;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

public class SimpleMessageMarkupTemplateProvider extends AbstractResourceTemplateProvider<String, MessageResponse> implements MarkupTemplateProvider {

	public static final String MESSAGE_AREA = "<!-- Message Content -->";
	
	private final Function<Content, String> converter;
	
	public SimpleMessageMarkupTemplateProvider(String templatePrefix, String templateSuffix, ResourceLoader rl, Function<Content, String> converter) {
		super(templatePrefix, templateSuffix, rl);
		this.converter = converter;
	}

	@Override
	protected String deserializeTemplate(InputStream is) throws IOException {
		return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
	}

	@Override
	protected String applyTemplate(String template, MessageResponse t) {
		String markup = converter.apply(t.getMessage());
		return template.replace(MESSAGE_AREA, markup);
	}

	@Override
	protected String getDefaultTemplate(MessageResponse r) {
		String out = getTemplateForName("default");
		return out == null ? MESSAGE_AREA : out;
	}
	
	
}
