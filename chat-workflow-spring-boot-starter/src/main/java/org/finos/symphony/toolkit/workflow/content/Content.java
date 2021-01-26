package org.finos.symphony.toolkit.workflow.content;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
		} else if (this instanceof OrderedContent) {
			return StreamSupport.stream(((OrderedContent<Content>) this).spliterator(), false)
				.flatMap(i -> i.only(x).stream())
				.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * Returns the content structure either unaltered (if item doesn't exist within it)
	 * or a new copy without item, or null (if item was the current content item).
	 */
	public default Content without(Content item) {
		if (item.equals(this)) {
			return null;
		} else if (this instanceof OrderedContent<?>) {
			@SuppressWarnings("unchecked")
			OrderedContent<Content> oc = (OrderedContent<Content>) this;
			List<Content> elements = oc.getContents().stream()
				.map(e -> (Content) e.without(item))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			
			return oc.buildAnother(elements);
		} else {
			return this;
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
	
	static class Parts<X> {
		
		X removed;
		
		
		
	}
}
