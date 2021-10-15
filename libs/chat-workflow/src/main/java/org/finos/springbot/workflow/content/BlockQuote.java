package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface BlockQuote extends OrderedContent<Content> {

	public static BlockQuote of(String str) {
		return of(
				Arrays.stream(str.split("\\n"))
					.map(s -> Paragraph.of(s))
					.toArray(Content[]::new));
	}
	
	public static BlockQuote of(Content... c) {
		return new BlockQuoteImpl(Arrays.asList(c));
	}
		
	public static class BlockQuoteImpl extends AbstractOrderedContent<Content> implements BlockQuote {
		
		public BlockQuoteImpl(List<Content> c) {
			super(c);
		}
		
		@Override
		public String toString() {
			return "BlockQuote ["+getContents().toString()+"]";
		}

		@Override
		public BlockQuote buildAnother(List<Content> contents) {
			return new BlockQuoteImpl(contents);
		}

		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof BlockQuote;
		}
	}

}
