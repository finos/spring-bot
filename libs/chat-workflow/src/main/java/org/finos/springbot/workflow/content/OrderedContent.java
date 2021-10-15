package org.finos.springbot.workflow.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * For any content where it is constructed of multiple sub-elements, 
 * such as lists (containing list items), paragraphs (containing words) etc.
 * 
 * @author rob@kite9.com
 *
 */
public interface OrderedContent<C extends Content> extends Content, Iterable<C>  {

	List<C> getContents();
	
	public default Iterator<C> iterator() {
		return getContents().iterator();
	}

	public OrderedContent<C> buildAnother(List<C> contents);
	
	public default int size() {
		return getContents().size();
	}
	
	
	public default Content removeAtStart(Content item) {
		if (matches(item)) {
			return null;
		}
			
		if (size() > 0) {
			Content first = getContents().get(0);
					
			if (first.startsWith(item)) {
				@SuppressWarnings("unchecked")
				C newFirst = (C) first.removeAtStart(item);
				List<C> sublist = getContents().subList(1, size());
				if (newFirst != null) {
					sublist = new ArrayList<C>(sublist);
					sublist.add(0, newFirst);
				}
				return buildAnother(sublist);
			}
		}
			
		return this;
	}
	
	public default Content replace(Content oldContent, Content newContent) {
		if (this.matches(oldContent)) {
			return newContent; 
		} 
		
		if (size() > 0) {
			List<C> done = new ArrayList<>(getContents());
			for (int i = 0; i < done.size(); i++) {
				Content ci = done.get(i);
				@SuppressWarnings("unchecked")
				C newCi = (C) ci.replace(oldContent, newContent);
				if (!newCi.equals(ci)) {
					done.set(i, newCi);
					return buildAnother(done);
				}
			}
		}
			
		return this;
 	}
	
	public default boolean startsWith(Content item) {
		if (matches(item)) {
			return true;
		}
		
		if (size() > 0) {
			return getContents().get(0).startsWith(item);	
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public default <X extends Content> List<X> only(Class<X> x) {
		if (x.isAssignableFrom(this.getClass())) {
			return Collections.singletonList((X) this);
		} 
		
		return StreamSupport.stream(this.spliterator(), false)
				.flatMap(i -> i.only(x).stream())
				.collect(Collectors.toList());
	}
	
	
	public default Content without(Content item) {
		if (item.matches(this)) {
			return null;
		} 
		
		@SuppressWarnings("unchecked")
		List<C> elements = (List<C>) getContents().stream()
				.map(e -> e.without(item))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			
		return buildAnother(elements);
	}
	
	/**
	 * Visitor pattern - visits the container and all the child objects, depth-first. 
	 */
	public default void visit(Consumer<Content> visitor) {
		visitor.accept(this);
		getContents().stream().forEach(i -> visitor.accept(i));
	}
	 
}
