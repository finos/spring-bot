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
		return new Paragraph() {

			@Override
			public List<Content> getContents() {
				return c;
			}

			@Override
			public int hashCode() {
				return c.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Paragraph) {
					return getContents().equals(((Paragraph) obj).getContents());
				} else {
					return false;
				}
			}

			@Override
			public String toString() {
				return "Paragraph ["+c.toString()+"]";
			}


			@Override
			public String getText() {
				return getContents().stream()
					.map(e -> e.getText())
					.reduce("", (a, b) -> a + " " + b);
			}
			

			@Override
			public Paragraph buildAnother(List<Content> contents) {
				return of(contents);
			}
		};
	}
}
