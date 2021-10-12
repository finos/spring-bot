package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

public interface OrderedList extends OrderedContent<Paragraph> {

	public static OrderedList of(List<Paragraph> c) {
		abstract class OrderedListOut extends AbstractOrderedContent<Paragraph> implements OrderedList {
			public OrderedListOut(List<Paragraph> c) {
				super(c);
			}			
		}
 		
		return new OrderedListOut(c) {

			@Override
			public String toString() {
				return "OrderedList ["+c.toString()+"]";
			}

			@Override
			public OrderedList buildAnother(List<Paragraph> contents) {
				return of(contents);
			}
		};
	}
}
