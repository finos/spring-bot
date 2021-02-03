package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

public interface UnorderedList extends OrderedContent<Paragraph> {

	public static UnorderedList of(List<Paragraph> c) {
		return new UnorderedList() {

			@Override
			public List<Paragraph> getContents() {
				return c;
			}

			@Override
			public int hashCode() {
				return c.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof UnorderedList) {
					return getContents().equals(((UnorderedList) obj).getContents());
				} else {
					return false;
				}
			}

			@Override
			public String toString() {
				return "UnorderedList ["+c.toString()+"]";
			}


			@Override
			public String getText() {
				return getContents().stream()
					.map(e -> e.getText())
					.reduce("", (a, b) -> a + " " + b);
			}
			

			@Override
			public UnorderedList buildAnother(List<Paragraph> contents) {
				return UnorderedList.of(contents);
			}
		};
	}
}
