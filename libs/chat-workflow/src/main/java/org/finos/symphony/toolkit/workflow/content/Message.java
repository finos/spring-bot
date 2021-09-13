package org.finos.symphony.toolkit.workflow.content;

import java.util.Iterator;
import java.util.List;

public interface Message extends Paragraph {

	public static <X extends Content> Message of(List<X> c) {
		return new Message() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Content> getContents() {
				return (List<Content>) c;
			}

			@Override
			public int hashCode() {
				return c.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Message) {
					return getContents().equals(((Message) obj).getContents());
				} else {
					return false;
				}
			}

			@Override
			public String toString() {
				return "Message ["+c.toString()+"]";
			}

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<Content> iterator() {
				return (Iterator<Content>) c.iterator();
			}

			@Override
			public String getText() {
				return getContents().stream()
						.map(e -> e.getText())
						.reduce("", (a, b) -> a + "\n" + b);
			}

			@Override
			public Message buildAnother(List<Content> contents) {
				return of(contents);
			}
		};
	}
	
}
