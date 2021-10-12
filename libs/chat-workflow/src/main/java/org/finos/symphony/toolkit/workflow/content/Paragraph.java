package org.finos.symphony.toolkit.workflow.content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Paragraph extends OrderedContent<Content> {
	
	public static <X extends Content> Paragraph of(String str) {
		return of(
				Arrays.stream(str.split("\\s"))
					.map(s -> Word.of(s))
					.collect(Collectors.toList()));
	}

	public static Paragraph of(List<Content> c) {
		
		abstract class ParagraphOut extends AbstractOrderedContent<Content> implements Paragraph {

			public ParagraphOut(List<Content> c) {
				super(c);
			}
			
		}
 		
		return new ParagraphOut(c) {

			@Override
			public String toString() {
				return "Paragraph ["+c.toString()+"]";
			}

			@Override
			public Paragraph buildAnother(List<Content> contents) {
				return of(contents);
			}
		};
	}
}
