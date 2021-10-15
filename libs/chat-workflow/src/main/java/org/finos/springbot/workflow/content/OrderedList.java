package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface OrderedList extends OrderedContent<Paragraph> {
	
	public static class OrderedListImpl extends AbstractOrderedContent<Paragraph> implements OrderedList {
	
		public OrderedListImpl(List<Paragraph> c) {
			super(c);
		}	
		
		@Override
		public String toString() {
			return "OrderedList ["+getContents().toString()+"]";
		}

		@Override
		public OrderedList buildAnother(List<Paragraph> contents) {
			return new OrderedListImpl(contents);
		}
		
		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof OrderedList;
		}
	}

	public static OrderedList of(Paragraph... c) {
		return new OrderedListImpl(Arrays.asList(c));
	}
}
