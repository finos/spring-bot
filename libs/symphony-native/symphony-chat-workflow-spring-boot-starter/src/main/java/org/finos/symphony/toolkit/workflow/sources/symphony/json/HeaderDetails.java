package org.finos.symphony.toolkit.workflow.sources.symphony.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

/**
 * Used for formatting the header of the bot's messages.
 * 
 * @author moffrob
 *
 */
@Work(jsonTypeName = {"", "org.finos.symphony.toolkit.workflow.form.headerDetails"}, index = false)
public class HeaderDetails {
	
	public static final String KEY = "header";

	private String name;
	private String description;
	private List<HashTag> tags = new ArrayList<HashTag>();
	
	public HeaderDetails() {
		super();
	}
	
	public HeaderDetails(String name, String description, List<HashTag> tags) {
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
	public List<HashTag> getTags() {
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
