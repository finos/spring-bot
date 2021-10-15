package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface Message extends Paragraph {
	
	public static <X extends Content> Message of(String str) {
		return of(
				Arrays.stream(str.split("\\n"))
					.map(s -> Paragraph.of(s))
					.toArray(Content[]::new));
	}
	
	public class MessageImpl extends AbstractOrderedContent<Content> implements Message {

		@SuppressWarnings("unchecked")
		public MessageImpl(List<? extends Content> c) {
			super((List<Content>) c);
		}		
		
		@Override
		public String toString() {
			return "Message ["+getContents().toString()+"]";
		}

		@Override
		public Message buildAnother(List<Content> contents) {
			return new MessageImpl(contents);
		}
		
		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof Message;
		}
	}

	public static Message of(Content... c) {
		return new MessageImpl(Arrays.asList(c));		
	}
	
}
