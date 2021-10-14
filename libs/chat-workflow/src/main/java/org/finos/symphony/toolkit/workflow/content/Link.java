package org.finos.symphony.toolkit.workflow.content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Link extends OrderedContent<Content> {

	public String getHRef();
	
	
	public static <X extends Content> Link of(String href, String text) {
		return of(href,
				Arrays.stream(text.split("\\s"))
					.map(s -> Word.of(s))
					.collect(Collectors.toList()));
	}

	public static Link of(String href, List<Content> c) {
		
		abstract class LinkOut extends AbstractOrderedContent<Content> implements Link {

			public LinkOut(List<Content> c) {
				super(c);
			}
			
		}
 		
		return new LinkOut(c) {

			@Override
			public String toString() {
				return "Link ["+c.toString()+"]";
			}

			@Override
			public Link buildAnother(List<Content> contents) {
				return of(href, contents);
			}

			@Override
			public String getHRef() {
				return href;
			}
		};
	}
}
