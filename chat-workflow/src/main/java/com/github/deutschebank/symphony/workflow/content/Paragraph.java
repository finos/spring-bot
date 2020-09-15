package com.github.deutschebank.symphony.workflow.content;

import java.util.Iterator;
import java.util.List;

public interface Paragraph extends Content, Iterable<Content> {

	List<Content> getContents();
	

	public static Paragraph of(List<Content> c) {
		return new Paragraph() {

			@Override
			public List<Content> getContents() {
				return c;
			}

			@Override
			public int hashCode() {
				return c.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Paragraph) {
					return getContents().equals(((Paragraph) obj).getContents());
				} else {
					return false;
				}
			}

			@Override
			public String toString() {
				return "Paragraph ["+c.toString()+"]";
			}

			@Override
			public Iterator<Content> iterator() {
				return c.iterator();
			}

			@Override
			public String getText() {
				return getContents().stream()
					.map(e -> e.getText())
					.reduce("", (a, b) -> a + " " + b);
			}
		};
	}
}
