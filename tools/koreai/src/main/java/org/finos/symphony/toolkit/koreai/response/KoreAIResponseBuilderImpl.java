package org.finos.symphony.toolkit.koreai.response;

import static org.finos.symphony.toolkit.koreai.response.KoreAIResponse.MESSAGE_ML;
import static org.finos.symphony.toolkit.koreai.response.KoreAIResponse.TEMPLATE_TYPE;
import static org.finos.symphony.toolkit.koreai.response.KoreAIResponse.TEXT;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
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
	
	static class SymphonyMessageMLNodeRenderer implements NodeRenderer {

	    private final HtmlWriter html;
	    private final HtmlNodeRendererContext context;

	    SymphonyMessageMLNodeRenderer(HtmlNodeRendererContext context) {
	        this.html = context.getWriter();
	        this.context = context;
	    }
	    
	    protected void visitChildren(Node parent) {
	        Node node = parent.getFirstChild();
	        while (node != null) {
	            Node next = node.getNext();
	            context.render(node);
	            node = next;
	        }
	    }


	    @Override
	    public Set<Class<? extends Node>> getNodeTypes() {
	        // Return the node types we want to use this renderer for.
	        Set<Class<? extends Node>> out = new HashSet<Class<? extends Node>>();
	        out.add(Emphasis.class);
	        out.add(StrongEmphasis.class);
	        out.add(Image.class);
	        out.add(FencedCodeBlock.class);
	        return out;
	    }

	    @Override
	    public void render(Node node) {
	    	
	    	if (node.getClass() == Emphasis.class) {
	    		html.tag("i");
	    		visitChildren(node);
	    		html.tag("/i");
	    	} else if (node.getClass() == StrongEmphasis.class) {
	    		html.tag("b");
	    		visitChildren(node);
	    		html.tag("/b");
	    	} else if (node.getClass() == FencedCodeBlock.class) {
	    		html.tag("code");
	    		html.text(((FencedCodeBlock)node).getLiteral());
	    		html.tag("/code");
	    	} else if (node.getClass() == Image.class) {
				String url = ((Image) node).getDestination();

				Map<String, String> attrs = new LinkedHashMap<>();
				if (context.shouldSanitizeUrls()) {
					url = context.urlSanitizer().sanitizeImageUrl(url);
				}

				attrs.put("src", context.encodeUrl(url));
				html.tag("img", attrs, true);
			}
	    }
	}

	JsonNodeFactory jnf;
	ObjectMapper om;
	Parser p = Parser.builder().build();
	HtmlRenderer r = HtmlRenderer.builder().softbreak("<br />").nodeRendererFactory(new HtmlNodeRendererFactory() {
			public NodeRenderer create(HtmlNodeRendererContext context) {
				return new SymphonyMessageMLNodeRenderer(context);
			}
		}).build();

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
	
	public static final Pattern OPTION = Pattern.compile("^[a-z]\\) (.*)$");

	protected ObjectNode convertToMessageMlAndOptions(ObjectNode n) {
		TextNode tn = (TextNode) n.get(TEXT);
		ArrayNode options = jnf.arrayNode();
		n.set(KoreAIResponse.OPTIONS_ML, options);

		if (tn != null) {
			String[] multiline = tn.asText().split("[\n|\\n]");
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
