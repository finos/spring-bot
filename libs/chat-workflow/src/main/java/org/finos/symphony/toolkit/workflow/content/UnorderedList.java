package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

public interface UnorderedList extends OrderedContent<Paragraph> {

	public static UnorderedList of(List<Paragraph> c) {
		
		abstract class UnorderedListOut extends AbstractOrderedContent<Paragraph> implements UnorderedList {
			public UnorderedListOut(List<Paragraph> c) {
				super(c);
			}			
		}
 		
		return new UnorderedListOut(c) {

			@Override
			public String toString() {
				return "UnorderedList ["+c.toString()+"]";
			}

			@Override
			public UnorderedList buildAnother(List<Paragraph> contents) {
				return of(contents);
			}
		};
	}
}
