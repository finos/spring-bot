package com.github.deutschebank.symphony.workflow.form;

import java.util.Collection;
import java.util.List;

import com.github.deutschebank.symphony.workflow.content.HashTag;

/**
 * Used for formatting the header of the bot's messages.
 * 
 * @author moffrob
 *
 */
public class HeaderDetails {

	private String name;
	private String description;
	private Collection<HashTag> tags;
	
	public HeaderDetails() {
		super();
	}
	
	public HeaderDetails(String name, String description, Collection<HashTag> tags) {
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
	public Collection<HashTag> getTags() {
		return tags;
	}
	public void setTags(List<HashTag> tags) {
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
