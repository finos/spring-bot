package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface UnorderedList extends OrderedContent<Paragraph> {
	
	public static class UnorderedListImpl extends AbstractOrderedContent<Paragraph> implements UnorderedList {
		
		public UnorderedListImpl(List<Paragraph> c) {
			super(c);
		}		
		
		@Override
		public String toString() {
			return "UnorderedList ["+getContents().toString()+"]";
		}

		@Override
		public UnorderedList buildAnother(List<Paragraph> contents) {
			return new UnorderedListImpl(contents);
		}
		
		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof UnorderedList;
		}
	}
	
	public static UnorderedList of(Paragraph... c) {
		return new UnorderedListImpl(Arrays.asList(c));
	}
}
