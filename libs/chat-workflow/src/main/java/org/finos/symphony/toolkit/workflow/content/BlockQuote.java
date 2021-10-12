package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

public interface BlockQuote extends OrderedContent<Content> {

	public static BlockQuote of(List<Content> c) {
		
		abstract class BlockQuoteOut extends AbstractOrderedContent<Content> implements BlockQuote {
			public BlockQuoteOut(List<Content> c) {
				super(c);
			}
		}
		
		return new BlockQuoteOut(c) {

			@Override
			public String toString() {
				return "Paragraph ["+c.toString()+"]";
			}

			@Override
			public BlockQuote buildAnother(List<Content> contents) {
				return of(contents);
			}
		};
	}

}
