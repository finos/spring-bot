package org.finos.springbot.symphony.content.serialization;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.finos.springbot.symphony.content.CashTag;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.RoomName;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.messages.PresentationMLHandler;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.content.serialization.AbstractContentParser;
import org.finos.symphony.toolkit.json.EntityJson;
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
public class MessageMLParser extends AbstractContentParser<String, EntityJson>{
	
	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	private SAXParserFactory factory = SAXParserFactory.newInstance();
	
	static class TagFrame<X extends Tag> extends TextFrame<X> {

		String id;
		X contents;
		
		public TagFrame(String qName, X contents) {
			super(qName);
			this.contents = contents;
		}

		@Override
		public X getContents() {
			if ((contents instanceof SymphonyUser) && (contents.getName()==null)) {
				SymphonyUser su = (SymphonyUser) contents;
				su.getId().add(new DisplayName(bufferWithoutPrefix()));
			} else if ((contents instanceof SymphonyRoom) && (contents.getName() == null)) {
				SymphonyRoom sr = (SymphonyRoom) contents;
				sr.getId().add(new RoomName(bufferWithoutPrefix()));
			}
			
			return contents;
		}

		protected String bufferWithoutPrefix() {
			String out = buf.toString();
			if (out.startsWith(""+contents.getTagType().getPrefix())) {
				return out.substring(1);
			} else {
				return out;
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
		public boolean hasContent() {
			return true;
		}
		
		
	}
	
	public Message apply(String source) {
		return apply(source, new EntityJson());
	}

	public Message apply(String message, EntityJson jsonObjects) {
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
							push(new TagFrame<SymphonyUser>(qName, (SymphonyUser) o));
						} else if (o instanceof HashTag) {
							push(new TagFrame<HashTag>(qName, (HashTag) o));
						} else if (o instanceof CashTag) {
							push(new TagFrame<CashTag>(qName, (CashTag) o));
						} else {
							throw new UnsupportedOperationException();
						}
					} else if (isStartTable(qName, attributes)) {
						push(new TableFrame("table"));
					} else if (isStartParaListItemOrCell(qName, attributes)) {
						push(new ParagraphFrame(qName));
					} else if (isStartList(qName, attributes)) {
						push(new ListFrame(qName));
					} else if (isStartRow(qName, attributes)) {
						if (top instanceof TableFrame) {
							((TableFrame)top).newRow();
						} else {
							throw new UnsupportedOperationException();
						}
					} else if (isStartMessage(qName, attributes)) {
						if (top == null) {
							push(new MessageFrame("messageML"));
						}
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
