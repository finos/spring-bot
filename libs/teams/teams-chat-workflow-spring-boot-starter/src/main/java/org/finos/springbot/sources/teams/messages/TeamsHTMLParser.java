package org.finos.springbot.sources.teams.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.springbot.sources.teams.content.TeamsContent;
import org.finos.symphony.toolkit.workflow.content.BlockQuote;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Image;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedContent;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TeamsHTMLParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsHTMLParser.class);
		
	static abstract class Frame<X extends Content> {
		
		abstract X getContents();
		
		abstract boolean isEnding(String qName);
		
		Frame<?> parent;
		
		abstract void push(Content c);
		
		abstract boolean hasContent();
	}
	
	static abstract class ContainerFrame<X extends Content> extends Frame<X> {
		
		String qName;
		
		public ContainerFrame(String qName) {
			this.qName = qName;
		}

		@Override
		boolean isEnding(String qName) {
			return this.qName.equals(qName);
		}
		
		
	}
	
	static class MentionFrame extends TextFrame<TeamsContent> {

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
		boolean hasContent() {
			return true;
		}
		
		
	}
	
	static class IgnoredFrame extends TextFrame<Content> {
		
		public IgnoredFrame(String tag) {
			super(tag);
		}

		@Override
		Content getContents() {
			return null;
		}

		@Override
		void push(Content c) {			
		}

		@Override
		boolean hasContent() {
			return false;
		}
		
	}
	
	static class BlockQuoteFrame extends TextRunFrame<BlockQuote> {

		public BlockQuoteFrame(String tag) {
			super(tag);
		}

		public BlockQuote getContents() {
			consumeBuffer();
			return BlockQuote.of(stuffSoFar);
				
		}
		
	}
	
	static class ListFrame extends ContainerFrame<OrderedContent<?>> {

		private List<Paragraph> contents = new ArrayList<>();
		
		public ListFrame(String qName) {
			super(qName);
		}

		public OrderedContent<?> getContents() {
			if ("ol".equals(qName)) {
				return OrderedList.of(contents);
			} else {
				return UnorderedList.of(contents);
			}
		}

		@Override
		void push(Content c) {
			if (c instanceof Paragraph) {
				contents.add((Paragraph) c);
			} else {
				throw new UnsupportedOperationException("Only <li> can appear in <"+qName+">");
			}
		}

		@Override
		boolean hasContent() {
			return contents.size() > 0;
		}
		
	}
	
	static class TableFrame extends ContainerFrame<Table> {
		
		public TableFrame(String qName) {
			super(qName);
		}

		private List<List<Content>> contents = new ArrayList<>();

		void newRow() {
			contents.add(new ArrayList<>());		
		}
		
		@Override
		public Table getContents() {
			return new Table() {
				
				@Override
				public List<List<Content>> getData() {
					return contents.subList(1, contents.size());
				}
				
				@Override
				public List<Content> getColumnNames() {
					return contents.get(0);
				}

				@Override
				public int hashCode() {
					return contents.hashCode();
				}

				@Override
				public boolean equals(Object obj) {
					if (obj instanceof Table) {
						return getData().equals(((Table) obj).getData()) &&
							getColumnNames().equals(((Table) obj).getColumnNames());
					} else {
						return false;
					}
				}

				@Override
				public String toString() {
					return "PastedTable ["+contents+"]";
				}

				@Override
				public String getText() {
					return "<pastedTable />";
				}

			};
		}

		@Override
		public void push(Content c) {
			List<Content> lastRow = contents.get(contents.size()-1);
			lastRow.add(c);
		}

		@Override
		boolean hasContent() {
			return contents.size()>0;
		}
		
	}
	
	static abstract class TextFrame<X extends Content> extends ContainerFrame<X> {
		
		public TextFrame(String qName) {
			super(qName);
		}

		StringBuilder buf = new StringBuilder();
		
		void push(char[] ch, int start, int length) {
			buf.append(ch, start, length);
		}
		
		void push(String s) {
			buf.append(s);
		}
	}
	
	static abstract class TextRunFrame<X extends Content> extends TextFrame<X> {
		
		public TextRunFrame(String qName) {
			super(qName);
		}

		List<Content> stuffSoFar = new ArrayList<>();
		
		void push(Content c) {
			consumeBuffer();
			stuffSoFar.add(c);
		}

		protected void consumeBuffer() {
			Arrays.stream(buf.toString().split("\\s+"))
			.filter(s -> s.length() > 0)
			.map(s -> Word.of(s))
			.forEach(w -> stuffSoFar.add(w));
			
			buf.setLength(0);
		}

		@Override
		boolean hasContent() {
			return stuffSoFar.size() > 0;
		}
	}
	
	static class MessageFrame extends TextRunFrame<Message> {
				
		public MessageFrame(String tag) {
			super(tag);
		}
		
		public Message getContents() {
			consumeBuffer();
			return Message.of(stuffSoFar);
				
		}
	
	}
	
	static class CodeBlockFrame extends TextFrame<CodeBlock> {
		
		public CodeBlockFrame(String tag) {
			super(tag);
		}

		@Override
		CodeBlock getContents() {
			return CodeBlock.of(buf.toString());
		}

		@Override
		void push(Content c) {
			// TODO Auto-generated method stub
			
		}

		@Override
		boolean hasContent() {
			return buf.length() > 0;
		}
		
	}
		
	static class ParagraphFrame extends TextRunFrame<Paragraph> {
		
		public ParagraphFrame(String qName) {
			super(qName);
		}

		public Paragraph getContents() {
			consumeBuffer();
			return Paragraph.of(stuffSoFar);
				
		}
	}
	
	public Message parse(String message, List<Entity> ctx) {

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
