package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface Paragraph extends OrderedContent<Content> {
	
	public static <X extends Content> Paragraph of(String str) {
		return of(
				Arrays.stream(str.split("\\s"))
					.map(s -> Word.of(s))
					.toArray(Word[]::new));
	}
	
	public static class ParagraphImpl extends AbstractOrderedContent<Content> implements Paragraph {

		public ParagraphImpl(List<Content> c) {
			super(c);
		}
		
		@Override
		public String toString() {
			return "Paragraph ["+getContents().toString()+"]";
		}

		@Override
		public Paragraph buildAnother(List<Content> contents) {
			return new ParagraphImpl(contents);
		}
		
		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof Paragraph;
		}
	}

	public static Paragraph of(Content... c) {
		return new ParagraphImpl(Arrays.asList(c));
	}
}
