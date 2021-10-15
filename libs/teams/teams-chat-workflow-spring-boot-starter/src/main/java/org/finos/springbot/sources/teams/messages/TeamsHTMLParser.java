package org.finos.springbot.sources.teams.messages;

import java.util.List;
import java.util.Map;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.springbot.sources.teams.content.TeamsContent;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Image;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.serialization.AbstractContentParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.bot.schema.Entity;


/**
 * Provides functionality for simple command messages.  i.e. those likely to have been typed in by users.
 * Will deliberately barf when it encounters tables, or lists, or something that is not just a list of words and hash/cash/mention tags on a line.
 * 
 * @author Rob Moffat
 *
 */
public class TeamsHTMLParser extends AbstractContentParser<String, List<Entity>> {
	
	protected static class MentionFrame extends TextFrame<TeamsContent> {

		String id;
		Entity e;

		public MentionFrame(String qName, Entity e) {
			super(qName);
			this.e = e;
		}

		@Override
		public TeamsContent getContents() {
			Map<String, JsonNode> props = e.getProperties();
			ObjectNode on = (ObjectNode) props.get("mentioned");
			String id = on.get("id").asText();
			String name = on.get("name").asText();
			TeamsChat out = new TeamsChat(id, name);
			return out;
		}

		@Override
		public void push(Content c) {
			throw new UnsupportedOperationException("Can't nest content in tag");
		}

		@Override
		public boolean hasContent() {
			return true;
		}

	}
	
	public Message apply(String message, List<Entity> ctx) {

		Content [] out = { null };
		
		Document d = Jsoup.parse(message);
		
		d.traverse(new NodeVisitor() {
			
			Frame<?> top = null;
			
			
			@Override
			public void head(Node node, int depth) {
				if (node instanceof Element) {
					startElement(((Element)node).tagName(), node.attributes());
				} else if (node instanceof TextNode) {
					characters(((TextNode) node).getWholeText());
				}
			}
				
					
			public void startElement(String qName, Attributes attributes) {		
				if (top instanceof CodeBlockFrame) {
					push(new IgnoredFrame(qName));
				} else if (isStartCodeBlock(qName, attributes)) {
					push(new CodeBlockFrame(qName));
				} else if (isStartMention(qName, attributes)) {
					String entityId = attributes.get("itemid");
					int intEntityId = Integer.parseInt(entityId);
					Entity e = ctx.get(intEntityId);
					push(new MentionFrame(qName, e));
				} else if (isStartTable(qName, attributes)) {
					push(new TableFrame(qName));
				} else if (isStartParaListItemOrCell(qName, attributes)) {
					push(new ParagraphFrame(qName));
				} else if (isStartList(qName, attributes)) {
					push(new ListFrame(qName));
				} else if (isBlockQuote(qName, attributes)) {
					push(new BlockQuoteFrame(qName));
				} else if (isImage(qName, attributes)) {
					top.push(Image.of(attributes.get("src"), attributes.get("alt")));
				} else if (isStartRow(qName, attributes)) {
					if (top instanceof TableFrame) {
						((TableFrame)top).newRow();
					} else {
						throw new UnsupportedOperationException();
					}
				} else if (isStartMessage(qName, attributes)) {
					if (top == null) {
						push(new MessageFrame(qName));
					}
				}
			}
			
			@Override
			public void tail(Node node, int depth) {
				if (node instanceof Element) {
					endElement(((Element) node).tagName());
				} 
			}
			
			private boolean isImage(String qName, Attributes attributes) {
				return "img".equals(qName);
			}

			private boolean isStartMessage(String qName, Attributes attributes) {
				return "html".equals(qName);
			}

			private boolean isStartList(String qName, Attributes attributes) {
				return "ul".equals(qName) || "ol".equals(qName);
			}

			private boolean isStartRow(String qName, Attributes attributes) {
				return "tr".equals(qName);
			}
			
			private boolean isStartCodeBlock(String qName, Attributes attributes) {
				return "pre".equals(qName) || "code".equals(qName);
			}
			
			private boolean isBlockQuote(String qName, Attributes attributes) {
				return "blockquote".equals(qName);
			}

			private <X extends Frame<?>> X push(X newFrame) {
				newFrame.parent = top;
				top = newFrame;
				return newFrame;
			}


			private boolean isStartTable(String qName, Attributes attributes) {
				return "table".equals(qName);
			}

			private boolean isStartMention(String qName, Attributes attributes) {
				return "span".equals(qName) && ("http://schema.skype.com/Mention".equals(attributes.get("itemtype")));
			}
			
			private boolean isStartParaListItemOrCell(String qName, Attributes attributes) {
				return "p".equals(qName) || "td".equals(qName) || "li".equals(qName) || "th".equals(qName);
			}
			
			public void endElement(String qName) {
				if (top.isEnding(qName)) {
					Frame<?> parent = top.parent;
					Content c = top.getContents();
					if (parent == null) {
						out[0] = c;
					} else {
						top = parent;
						top.push(c);
					}
				}
			}

			public void characters(String s) {
				if (top instanceof TextFrame) {
					((TextFrame<?>) top).push(s);
				} else {
					if (!s.trim().isEmpty()) {
						throw new UnsupportedOperationException("Wasn't expecting text: "+s);
					}
				}
			}

				
		});
					
		return (Message) out[0];
	}

}
