package org.finos.symphony.toolkit.koreai.response;

import static org.finos.symphony.toolkit.koreai.response.KoreAIResponse.MESSAGE_ML;
import static org.finos.symphony.toolkit.koreai.response.KoreAIResponse.TEMPLATE_TYPE;
import static org.finos.symphony.toolkit.koreai.response.KoreAIResponse.TEXT;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class KoreAIResponseBuilderImpl implements KoreAIResponseBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(KoreAIResponseBuilderImpl.class);

	JsonNodeFactory jnf;
	ObjectMapper om;
	Parser p = Parser.builder().build();


	public KoreAIResponseBuilderImpl(ObjectMapper om, JsonNodeFactory instance) {
		super();
		this.om = om;
		this.jnf = instance;
	}

	@Override
	public KoreAIResponse formatResponse(String json) {
		JsonNode out = parseJson(json);
		KoreAIResponse r = new KoreAIResponse();
		r.setOriginal(out);

		List<TextNode> elems = getTextElements(out);

		List<ObjectNode> processElements = elems.stream()
				.map(tn -> convertToPayload(tn))
				.map(n -> convertToMessageMlAndOptions(n))
				.collect(Collectors.toList());

		r.setProcessed(processElements);

		return r;
	}

	protected JsonNode parseJson(String json) {
		try {
			return om.readTree(json);
		} catch (JsonProcessingException e) {
			LOG.error("Coudln't parse JSON message from KoreAI", e);
			return jnf.objectNode().set(TEXT, jnf.textNode("Couldn't parse template: " + e.getMessage()));
		}
	}
	
	public static final Pattern OPTION = Pattern.compile("[a-z]\\) (.*)");

	protected ObjectNode convertToMessageMlAndOptions(ObjectNode n) {
		TextNode tn = (TextNode) n.get(TEXT);
		ArrayNode options = jnf.arrayNode();
		n.set(KoreAIResponse.OPTIONS_ML, options);

		if (tn != null) {
			String[] multiline = tn.asText().split("\n");
			StringBuilder text = new StringBuilder();
			for (String string : multiline) {
				Matcher m = OPTION.matcher(string);
				if (m.find()) {
					ObjectNode option = jnf.objectNode();
					String t = m.group(1);
					options.add(option);
					option.set(KoreAIResponse.TEXT, jnf.textNode(t));
				} else {
					text.append(string);
					text.append("\n");
				}
			}
			n.set(MESSAGE_ML, jnf.textNode(toMarkup(text.toString())));
		}
		return n;
	}

	public String toMarkup(String in) {
		Node document = p.parse(in);
		HtmlRenderer r = HtmlRenderer.builder().build();
		String markup = r.render(document);
		return markup;
	}

	protected ObjectNode convertToPayload(TextNode elem) {
		String txt = elem.asText();
		if (txt.startsWith("{\"type\":\"")) {
			JsonNode jn = parseJson(txt);
			ObjectNode out = (ObjectNode) jn.get("payload");
			if (!out.has("template_type")) {
				out.set("template_type", jn.get("type"));
			}
			return out;
		}

		if (txt.startsWith("{\"text\":\"")) {
			ObjectNode jn = (ObjectNode) parseJson(txt);
			elem = (TextNode) jn.get("text");
		}

		ObjectNode out = jnf.objectNode();
		out.set(TEXT, elem);
		out.set(TEMPLATE_TYPE, jnf.textNode("message"));
		return out;
	}

	protected List<TextNode> getTextElements(JsonNode in) {
		JsonNode text = in.get(TEXT);
		if (text instanceof TextNode) {
			return Collections.singletonList((TextNode) text);
		} else if (text instanceof ArrayNode) {
			return StreamSupport.stream(text.spliterator(), false).map(n -> (TextNode) n).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("Can't process " + in);
		}
	}


}
