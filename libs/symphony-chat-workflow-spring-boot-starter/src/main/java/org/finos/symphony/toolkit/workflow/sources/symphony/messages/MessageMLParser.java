package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedContent;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.CashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

import com.symphony.user.DisplayName;

/**
 * Provides functionality for simple command messages.  i.e. those likely to have been typed in by users.
 * Will deliberately barf when it encounters tables, or lists, or something that is not just a list of words and hash/cash/mention tags on a line.
 * 
 * @author Rob Moffat
 *
 */
public class MessageMLParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	private SAXParserFactory factory = SAXParserFactory.newInstance();
	
	static abstract class Frame<X extends Content> {
		
		abstract X getContents();
		
		abstract boolean isEnding(String qName);
		
		Frame<?> parent;
		
		abstract void push(Content c);
		
		abstract boolean hasContent();
	}
	
	static class TagFrame<X extends Tag> extends TextFrame<X> {

		String id;
		Tag.Type type;
		X contents;
		
		public TagFrame(X contents) {
			super();
			this.contents = contents;
		}

		@Override
		public X getContents() {
			if ((contents instanceof SymphonyUser) && (contents.getName()==null)) {
				SymphonyUser su = (SymphonyUser) contents;
				su.getId().add(new DisplayName(buf.toString()));
			}
			
			return contents;
		}
		
		@Override
		public boolean isEnding(String qName) {
			return true;
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
		
		String tag;

		public IgnoredFrame(String tag) {
			super();
			this.tag = tag;
		}
		
		@Override
		boolean isEnding(String qName) {
			return tag.equals(qName);
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
	
	static class CodeBlockFrame extends TextFrame<CodeBlock> {
		
		String tag;

		public CodeBlockFrame(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		CodeBlock getContents() {
			return CodeBlock.of(buf.toString());
		}

		@Override
		boolean isEnding(String qName) {
			return tag.equals(qName);
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
	
	static class ListFrame extends Frame<OrderedContent<?>> {

		private String qName;
		private List<Paragraph> contents = new ArrayList<>();
		
		public ListFrame(String qName) {
			this.qName = qName;
		}

		public OrderedContent<?> getContents() {
			if ("ol".equals(qName)) {
				return OrderedList.of(contents);
			} else {
				return UnorderedList.of(contents);
			}
		}

		@Override
		boolean isEnding(String qName) {
			return true;
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
	
	static class TableFrame extends Frame<Table> {
		
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
		public boolean isEnding(String qName) {
			return qName.equals("table");
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
	
	static abstract class TextFrame<X extends Content> extends Frame<X> {
		
		StringBuilder buf = new StringBuilder();
		
		void push(char[] ch, int start, int length) {
			buf.append(ch, start, length);
		}
	}
	
	static abstract class TextRunFrame<X extends Content> extends TextFrame<X> {
		
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
		boolean isEnding(String qName) {
			return true;
		}

		@Override
		boolean hasContent() {
			return stuffSoFar.size() > 0;
		}
	}
	
	static class MessageFrame extends TextRunFrame<Message> {
		
		public Message getContents() {
			consumeBuffer();
			return Message.of(stuffSoFar);
				
		}
		
		@Override
		boolean isEnding(String qName) {
			return true;
		}

		
	}
	
	static class ParagraphFrame extends TextRunFrame<Paragraph> {
		public Paragraph getContents() {
			consumeBuffer();
			return Paragraph.of(stuffSoFar);
				
		}
	}
	
	public Message parse(String source) {
		return parse(source, new EntityJson());
	}

	public Message parse(String message, EntityJson jsonObjects) {
		message = (!message.contains("<messageML>")) ? "<messageML>" + message + "</messageML>" : message;
		

		Content [] out = { null };
		
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(message)), new DefaultHandler2() {

				Frame<?> top = null;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					
					if (top instanceof CodeBlockFrame) {
						push(new IgnoredFrame(qName));
					} else if (isStartCodeBlock(qName, attributes)) {
						push(new CodeBlockFrame(qName));
					} else if (isStartTag(qName, attributes)) {
						String dataEntityId = attributes.getValue("data-entity-id");
						Object o = jsonObjects.get(dataEntityId);
						if (o instanceof SymphonyUser) {
							push(new TagFrame<SymphonyUser>((SymphonyUser) o));
						} else if (o instanceof HashTag) {
							push(new TagFrame<HashTag>((HashTag) o));
						} else if (o instanceof CashTag) {
							push(new TagFrame<CashTag>((CashTag) o));
						} else {
							throw new UnsupportedOperationException();
						}
					} else if (isStartTable(qName, attributes)) {
						push(new TableFrame());
					} else if (isStartParaListItemOrCell(qName, attributes)) {
						push(new ParagraphFrame());
					} else if (isStartList(qName, attributes)) {
						push(new ListFrame(qName));
					} else if (isStartRow(qName, attributes)) {
						if (top instanceof TableFrame) {
							((TableFrame)top).newRow();
						} else {
							throw new UnsupportedOperationException();
						}
					} else if (isStartMessage(qName, attributes)) {
						push(new MessageFrame());
					}
				}

				private boolean isStartMessage(String qName, Attributes attributes) {
					return "messageML".equals(qName) || ("div".equals(qName)  && "PresentationML".equals(attributes.getValue("data-format")));
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

				private <X extends Frame<?>> X push(X newFrame) {
					newFrame.parent = top;
					top = newFrame;
					return newFrame;
				}

				@Override
				public void startEntity(String name) throws SAXException {
					// do nothing 
				}

				@Override
				public void endEntity(String name) throws SAXException {
					// do nothing
				}

				private boolean isStartTable(String qName, Attributes attributes) {
					return "table".equals(qName);
				}

				private boolean isStartTag(String qName, Attributes attributes) {
					return "span".equals(qName) && (attributes.getValue("class") != null) && (attributes.getValue("class").contains("entity"));
				}
				
				private boolean isStartParaListItemOrCell(String qName, Attributes attributes) {
					return "p".equals(qName) || "td".equals(qName) || "li".equals(qName) || "th".equals(qName);
				}
				
				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
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

				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					if (top instanceof TextFrame) {
						((TextFrame<?>) top).push(ch, start, length);
					} else {
						String content = new String(ch, start, length);
						if (!content.trim().isEmpty()) {
							throw new UnsupportedOperationException("Wasn't expecting text: "+content);
						}
					}
				}

				@Override
				public void warning(SAXParseException e) throws SAXException {
					LOG.error("SAX warning: ", e);
				}

				@Override
				public void error(SAXParseException e) throws SAXException {
					LOG.error("SAX error: ", e);
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					LOG.error("SAX fatal error: ", e);
				}
				
			});
		
		} catch (Exception e) {
			throw new RuntimeException("Couldn't parse message: "+message, e);
		}

		
		return (Message) out[0];
	}

}
