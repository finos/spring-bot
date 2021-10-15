package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface Heading extends OrderedContent<Word> {
	
	int getLevel();
	
	public static Heading of(String str, int level) {
		return of(
				level,
				Arrays.stream(str.split("\\s"))
					.map(s -> Word.of(s))
					.toArray(Word[]::new));
	}
	
	public static class HeadingImpl extends AbstractOrderedContent<Word> implements Heading {
		
		private int level;
		
		public HeadingImpl(List<Word> c, int level) {
			super(c);
			this.level = level;
		}
		
		@Override
		public String toString() {
			return "Heading ["+getContents().toString()+"]";
		}
		
		public int getLevel() {
			return level;
		}

		@Override
		public Heading buildAnother(List<Word> contents) {
			return new HeadingImpl(contents, level);
		}
		
		
		
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && this.level == ((Heading)obj).getLevel();
		}

		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof Heading;
		}
	}

	public static Heading of(int level, Word... c) {
		return new HeadingImpl(Arrays.asList(c), level);
	}
}
