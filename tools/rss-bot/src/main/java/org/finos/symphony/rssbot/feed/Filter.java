package org.finos.symphony.rssbot.feed;

import java.util.function.Predicate;

import org.finos.symphony.toolkit.workflow.java.Work;

@Work(editable = true, instructions = "Create new filter for bot's messages in this room", name = "Filter")
public class Filter implements Predicate<String> {
	
	String toMatch;
	
	enum Type { INCLUDE, EXCLUDE }

	Type type = Type.EXCLUDE;

	public String getToMatch() {
		return toMatch;
	}

	public void setToMatch(String toMatch) {
		this.toMatch = toMatch;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public boolean test(String t) {
		boolean contains = t.toLowerCase().contains(toMatch.toLowerCase());
		switch (getType()) {
		case INCLUDE:
			return contains;
		case EXCLUDE:
		default: 
			return !contains;
		}
	}
	
}
