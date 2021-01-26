package org.finos.symphony.toolkit.workflow.content;

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
}
