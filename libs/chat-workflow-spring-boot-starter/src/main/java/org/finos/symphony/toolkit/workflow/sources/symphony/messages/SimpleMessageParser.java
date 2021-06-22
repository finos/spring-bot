package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.CashTagDef;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedContent;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.content.Tag.Type;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.symphonyoss.Taxonomy;
import org.symphonyoss.fin.security.id.SecId;
import org.symphonyoss.taxonomy.Hashtag;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.symphony.user.UserId;

/**
 * Provides functionality for simple command messages.  i.e. those likely to have been typed in by users.
 * Will deliberately barf when it encounters tables, or lists, or something that is not just a list of words and hash/cash/mention tags on a line.
 * 
 * @author Rob Moffat
 *
 */
public class SimpleMessageParser {
	
	private SAXParserFactory factory = SAXParserFactory.newInstance();
	
	static abstract class Frame<X extends Content> {
		
		abstract X getContents();
		
		abstract boolean isEnding(String qName);
		
		Frame<?> parent;
		
		abstract void push(Content c);
		
		abstract boolean hasContent();
	}
	
	static class TagFrame extends TextFrame<Tag> {

		String id;
		Tag.Type type;
		
		@Override
		public Tag getContents() {
			if (type== Type.USER) {
				return new UserDef(id, buf.substring(1), null);
			} else if (type == Type.CASH ){
				return new CashTagDef(id);
			} else {
				return new HashTagDef(id);
			}
		}
		
		private void deReference(Object o) {
			if (o instanceof Taxonomy) {
				o = ((Taxonomy)o).getId().get(0);
			} 
			
			if (o instanceof UserId) {
				id = ((UserId) o).getValue();
				type = Type.USER;
			} else if (o instanceof SecId) {
				id = ((SecId) o).getValue();
				type = Type.CASH;
			} else if (o instanceof Hashtag) {
				id = ((Hashtag) o).getValue();
				type = Type.HASH;
			} else if (o instanceof Tag) {
				id = ((Tag) o).getId();
				type = ((Tag) o).getTagType();
			} else {
				throw new UnsupportedOperationException();
			}
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
	
	static class TableFrame extends Frame<PastedTable> {
		
		private List<List<Content>> contents = new ArrayList<>();

		void newRow() {
			contents.add(new ArrayList<>());		
		}
		
		@Override
		public PastedTable getContents() {
			return new PastedTable() {
				
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
					if (obj instanceof PastedTable) {
						return getData().equals(((PastedTable) obj).getData()) &&
							getColumnNames().equals(((PastedTable) obj).getColumnNames());
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
	

	public Message parseMessage(String message, EntityJson jsonObjects) throws Exception {

		Content [] out = { null };
		
		SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(message)), new DefaultHandler() {

				Frame<?> top = null;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					
					if (isStartTag(qName, attributes)) {
						TagFrame tf = push(new TagFrame());
						String dataEntityId = attributes.getValue("data-entity-id");
						Object o = jsonObjects.get(dataEntityId);
						tf.deReference(o);
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

				private <X extends Frame<?>> X push(X newFrame) {
					newFrame.parent = top;
					top = newFrame;
					return newFrame;
				}

				private boolean isStartTable(String qName, Attributes attributes) {
					return "table".equals(qName);
				}

				private boolean isStartTag(String qName, Attributes attributes) {
					return "span".equals(qName) && attributes.getValue("class").contains("entity");
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
				
			});
		
		
		return (Message) out[0];
	}

}
