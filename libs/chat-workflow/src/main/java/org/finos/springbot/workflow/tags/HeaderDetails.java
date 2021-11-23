package org.finos.springbot.workflow.tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for formatting the header of the bot's messages.
 * 
 * @author moffrob
 *
 */
public class HeaderDetails {
	
	public static final String KEY = "header";

	private String name;
	private String description;
	private List<String> tags = new ArrayList<String>();
	
	public HeaderDetails() {
		super();
	}
	
	public HeaderDetails(String name, String description, List<String> tags) {
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
	
	public List<String> getTags() {
		return tags;
	}
	
	public void setTags(List<String> tags) { 
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
