package com.github.deutschebank.symphony.workflow.content;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Content {
	
	@JsonIgnore
	public String getText();

	public default <X extends Content> List<X> only(Class<X> x) {
		if (x.isAssignableFrom(this.getClass())) {
			return Collections.singletonList((X) this);
		} else if (this instanceof Iterable<?>) {
			return StreamSupport.stream(((Iterable<Content>) this).spliterator(), false)
				.flatMap(i -> i.only(x).stream())
				.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
	
	public default <X extends Content> Optional<X> getNth(Class<X> x, int n) {
		try {
			return Optional.of(only(x).get(n));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
