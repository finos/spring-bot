package org.finos.symphony.toolkit.workflow.content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Message extends Paragraph {
	
	public static <X extends Content> Message of(String str) {
		return of(
				Arrays.stream(str.split("\\n"))
					.map(s -> Paragraph.of(s))
					.collect(Collectors.toList()));
	}

	public static Message of(List<? extends Content> c) {
		
		abstract class MessageOut extends AbstractOrderedContent<Content> implements Message {

			@SuppressWarnings("unchecked")
			public MessageOut(List<? extends Content> c) {
				super((List<Content>) c);
			}			
		}
 		
		return new MessageOut(c) {

			@Override
			public String toString() {
				return "Message ["+c.toString()+"]";
			}

			@Override
			public Message buildAnother(List<Content> contents) {
				return of(contents);
			}
		};
		
	}
	
}
