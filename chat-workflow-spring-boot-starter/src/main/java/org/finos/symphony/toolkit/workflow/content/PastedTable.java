package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

/**
 * Represents a table pasted into a chat.
 * 
 * @author Rob Moffat
 *
 */
public interface PastedTable extends Content {

	public List<Content> getColumnNames();
	
	public List<List<Content>> getData();
}
