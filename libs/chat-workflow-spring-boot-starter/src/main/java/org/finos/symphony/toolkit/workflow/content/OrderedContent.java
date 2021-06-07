package org.finos.symphony.toolkit.workflow.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	public default boolean startsWith(Content item) {
		if (matches(item)) {
			return true;
		}
		
		if (size() > 0) {
			return getContents().get(0).startsWith(item);	
		}
		
		return false;
	}
	 
}
