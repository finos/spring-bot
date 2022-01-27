package org.finos.springbot.workflow.response.templating;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

public abstract class AbstractMarkupTemplateProvider<X extends Markup> extends AbstractResourceTemplateProvider<X, X, MessageResponse> implements MarkupTemplateProvider<X> {

	public static final String MESSAGE_AREA = "<!-- Message Content -->";
	
	private final BiFunction<Content, X, String> converter;
	
	public AbstractMarkupTemplateProvider(String templatePrefix, String templateSuffix, String defaultTemplateName, ResourceLoader rl, BiFunction<Content, X,  String> converter) {
		super(templatePrefix, templateSuffix, defaultTemplateName, rl);
		this.converter = converter;
	}

	@Override
	protected X deserializeTemplate(InputStream is) throws IOException {
		return toMarkup(StreamUtils.copyToString(is, StandardCharsets.UTF_8), null);
	}

	protected abstract X toMarkup(String s, X ctx);

	@Override
	protected X applyTemplate(X template, MessageResponse t) {
		X context = createContext(t);
		String markup = converter.apply(t.getMessage(), context);
		markup = prepareMarkupForInsertion(markup);
		return toMarkup(template.getContents().replace(MESSAGE_AREA, markup), context);
	}

	protected String prepareMarkupForInsertion(String markup) {
		return markup;
	}

	protected abstract X createContext(MessageResponse t);

	@Override
	protected X getDefaultTemplate(MessageResponse r) {
		X out = getTemplateForName(getDefaultTemplateName());
		return out == null ? toMarkup(MESSAGE_AREA, createContext(r)) : out;
	}
	
	
}
