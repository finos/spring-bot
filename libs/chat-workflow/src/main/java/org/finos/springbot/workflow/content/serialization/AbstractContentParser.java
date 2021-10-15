package org.finos.springbot.workflow.content.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.BlockQuote;
import org.finos.springbot.workflow.content.CodeBlock;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedContent;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.UnorderedList;
import org.finos.springbot.workflow.content.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets up a generic approach to parsing some kind of markup (messageML/HTML)
 * into the Content object structure.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractContentParser<T, U> implements BiFunction<T, U, Content> {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractContentParser.class);

	protected static abstract class Frame<X extends Content> {

		public abstract X getContents();

		public abstract boolean isEnding(String qName);

		public Frame<?> parent;

		public abstract void push(Content c);

		public abstract boolean hasContent();
	}

	protected static abstract class ContainerFrame<X extends Content> extends Frame<X> {

		String qName;

		public ContainerFrame(String qName) {
			this.qName = qName;
		}

		@Override
		public boolean isEnding(String qName) {
			return this.qName.equals(qName);
		}

	}

	protected static class IgnoredFrame extends TextFrame<Content> {

		public IgnoredFrame(String tag) {
			super(tag);
		}

		@Override
		public Content getContents() {
			return null;
		}

		@Override
		public void push(Content c) {
		}

		@Override
		public boolean hasContent() {
			return false;
		}

	}

	protected static class BlockQuoteFrame extends TextRunFrame<BlockQuote> {

		public BlockQuoteFrame(String tag) {
			super(tag);
		}

		public BlockQuote getContents() {
			consumeBuffer();
			return new BlockQuote.BlockQuoteImpl(stuffSoFar);
		}

	}

	protected static class ListFrame extends ContainerFrame<OrderedContent<?>> {

		private List<Paragraph> contents = new ArrayList<>();

		public ListFrame(String qName) {
			super(qName);
		}

		public OrderedContent<?> getContents() {
			if ("ol".equals(qName)) {
				return new OrderedList.OrderedListImpl(contents);
			} else {
				return new UnorderedList.UnorderedListImpl(contents);
			}
		}

		@Override
		public void push(Content c) {
			if (c instanceof Paragraph) {
				contents.add((Paragraph) c);
			} else {
				throw new UnsupportedOperationException("Only <li> can appear in <" + qName + ">");
			}
		}

		@Override
		public boolean hasContent() {
			return contents.size() > 0;
		}

	}

	protected static class TableFrame extends ContainerFrame<Table> {

		public TableFrame(String qName) {
			super(qName);
		}

		private List<List<Content>> contents = new ArrayList<>();

		public void newRow() {
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
						return getData().equals(((Table) obj).getData())
								&& getColumnNames().equals(((Table) obj).getColumnNames());
					} else {
						return false;
					}
				}

				@Override
				public String toString() {
					return "PastedTable [" + contents + "]";
				}

				@Override
				public String getText() {
					return "<pastedTable />";
				}

			};
		}

		@Override
		public void push(Content c) {
			List<Content> lastRow = contents.get(contents.size() - 1);
			lastRow.add(c);
		}

		@Override
		public boolean hasContent() {
			return contents.size() > 0;
		}

	}

	protected static abstract class TextFrame<X extends Content> extends ContainerFrame<X> {

		public TextFrame(String qName) {
			super(qName);
		}

		protected StringBuilder buf = new StringBuilder();

		public void push(char[] ch, int start, int length) {
			buf.append(ch, start, length);
		}

		public void push(String s) {
			buf.append(s);
		}
	}

	protected static abstract class TextRunFrame<X extends Content> extends TextFrame<X> {

		public TextRunFrame(String qName) {
			super(qName);
		}

		List<Content> stuffSoFar = new ArrayList<>();

		public void push(Content c) {
			consumeBuffer();
			stuffSoFar.add(c);
		}

		protected void consumeBuffer() {
			Arrays.stream(buf.toString().split("\\s+")).filter(s -> s.length() > 0).map(s -> Word.of(s))
					.forEach(w -> stuffSoFar.add(w));

			buf.setLength(0);
		}

		@Override
		public boolean hasContent() {
			return stuffSoFar.size() > 0;
		}
	}

	protected static class MessageFrame extends TextRunFrame<Message> {

		public MessageFrame(String tag) {
			super(tag);
		}

		public Message getContents() {
			consumeBuffer();
			return new Message.MessageImpl(stuffSoFar);

		}

	}

	protected static class CodeBlockFrame extends TextFrame<CodeBlock> {

		public CodeBlockFrame(String tag) {
			super(tag);
		}

		@Override
		public CodeBlock getContents() {
			return CodeBlock.of(buf.toString());
		}

		@Override
		public void push(Content c) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean hasContent() {
			return buf.length() > 0;
		}

	}

	protected static class ParagraphFrame extends TextRunFrame<Paragraph> {

		public ParagraphFrame(String qName) {
			super(qName);
		}

		public Paragraph getContents() {
			consumeBuffer();
			return new Paragraph.ParagraphImpl(stuffSoFar);

		}
	}

}