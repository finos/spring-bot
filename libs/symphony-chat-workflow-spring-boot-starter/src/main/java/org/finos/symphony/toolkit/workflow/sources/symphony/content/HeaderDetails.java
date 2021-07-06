package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.Collection;
import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Tag;

/**
 * Used for formatting the header of the bot's messages.
 * 
 * @author moffrob
 *
 */
@Work
public class HeaderDetails {

	private String name;
	private String description;
	private Collection<Tag> tags;
	
	public HeaderDetails() {
		super();
	}
	
	public HeaderDetails(String name, String description, Collection<Tag> tags) {
		super();
		this.name = name;
		this.description = description;
		this.tags = tags;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Collection<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
