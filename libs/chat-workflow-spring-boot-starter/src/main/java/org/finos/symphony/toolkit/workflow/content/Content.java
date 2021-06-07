package org.finos.symphony.toolkit.workflow.content;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Content {
	
	@JsonIgnore
	public String getText();

	/**
	 * Navigates through the content structure and returns a list of all Content objects
	 * of class x, in the order they occcur.
	 */
	@SuppressWarnings("unchecked")
	public default <X extends Content> List<X> only(Class<X> x) {
		if (x.isAssignableFrom(this.getClass())) {
			return Collections.singletonList((X) this);
		} else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * Returns the content structure either unaltered (if item doesn't exist within it)
	 * or a new copy without item, or null (if item was the current content item).
	 */
	public default Content without(Content item) {
		if (item.matches(this)) {
			return null;
		} else {
			return this;
		}
	}
	
	/**
	 * Returns content without item at the start. Returns the original object if the item at the start doesn't
	 * match, or returns a changed / null content if a match was made.
	 */
	public default Content removeAtStart(Content item) {
		if (this.matches(item)) {
			return null;
		} else {
			return this;
		}
	}
	
	/**
	 * Checks whether this content starts with item. 
	 */
	public default boolean startsWith(Content item) {
		if (this.matches(item)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Navigates through the content structure and returns a list of all Content objects
	 * of class x, and returns solely the nth one.
	 */
	public default <X extends Content> Optional<X> getNth(Class<X> x, int n) {
		try {
			return Optional.of(only(x).get(n));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Returns true if this content is the same as another piece of content.
	 */
	public default boolean matches(Content other) {
		return this.equals(other);
	}
	
	/**
	 * Visitor pattern
	 */
	public default void visit(Consumer<Content> visitor) {
		visitor.accept(this);
	}
}
