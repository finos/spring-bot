package org.finos.symphony.toolkit.workflow.content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Heading extends OrderedContent<Word> {
	
	int getLevel();
	
	public static Heading of(String str, int level) {
		return of(
				Arrays.stream(str.split("\\s"))
					.map(s -> Word.of(s))
					.collect(Collectors.toList()), 
				level);
	}

	public static Heading of(List<Word> c, int level) {
		
		abstract class HeadingOut extends AbstractOrderedContent<Word> implements Heading {
			public HeadingOut(List<Word> c) {
				super(c);
			}
		}
 		
		return new HeadingOut(c) {

			@Override
			public String toString() {
				return "Heading ["+c.toString()+"]";
			}
			
			public int getLevel() {
				return level;
			}

			@Override
			public Heading buildAnother(List<Word> contents) {
				return of(contents, level);
			}
		};
	}
}
