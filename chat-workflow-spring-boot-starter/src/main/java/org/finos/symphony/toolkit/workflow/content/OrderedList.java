package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

public interface OrderedList extends OrderedContent<Paragraph> {

	public static OrderedList of(List<Paragraph> c) {
		return new OrderedList() {

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
				if (obj instanceof OrderedList) {
					return getContents().equals(((OrderedList) obj).getContents());
				} else {
					return false;
				}
			}

			@Override
			public String toString() {
				return "OrderedList ["+c.toString()+"]";
			}


			@Override
			public String getText() {
				return getContents().stream()
					.map(e -> e.getText())
					.reduce("", (a, b) -> a + " " + b);
			}
			

			@Override
			public OrderedList buildAnother(List<Paragraph> contents) {
				return OrderedList.of(contents);
			}
		};
	}
}
