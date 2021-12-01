package org.finos.springbot.workflow.content;

import java.util.Arrays;
import java.util.List;

public interface Link extends OrderedContent<Content> {

	public String getHRef();
	
	public static <X extends Content> Link of(String href, String text) {
		return of(href,
				Arrays.stream(text.split("\\s"))
					.map(s -> Word.of(s))
					.toArray(Word[]::new));
	}
	
	public static class LinkImpl extends AbstractOrderedContent<Content> implements Link {

		private final String href;
		
		public LinkImpl(String href, List<Content> c) {
			super(c);
			this.href = href;
		}
		
		@Override
		public String toString() {
			return "Link ["+getContents().toString()+"]";
		}

		@Override
		public Link buildAnother(List<Content> contents) {
			return new LinkImpl(href, contents);
		}

		@Override
		public String getHRef() {
			return href;
		}
		
		@Override
		protected boolean rightClass(Object obj) {
			return obj instanceof Link;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && this.href.equals(((Link)obj).getHRef());
		}
		
		
	}

	public static Link of(String href, Content... c) {
		return new LinkImpl(href, Arrays.asList(c));
	}
}
