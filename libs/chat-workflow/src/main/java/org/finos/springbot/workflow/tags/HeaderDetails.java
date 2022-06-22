package org.finos.springbot.workflow.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.annotations.Work;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Used for formatting the header of the bot's messages.
 * 
 * @author moffrob
 *
 */
@Work(jsonTypeName = { 
		"org.finos.springbot.workflow.tags.headerDetails", 
		"org.finos.symphony.toolkit.workflow.form.headerDetails" },
	index = false)
public class HeaderDetails {
	
	public static class LegacyHeaderDeserialize extends StdConverter<Object, String> {

		@SuppressWarnings("unchecked")
		@Override
		public String convert(Object x) {
			if (x instanceof Map) {
				return (String) ((Map<String, Object>)x).get("name");
			} else if (x instanceof String) {
				return (String) x;
			} else {
				return null;
			}
		}		
	}
	
	public static final String KEY = "header";

	private String name;
	private String description;
	
	@JsonDeserialize(contentConverter =  LegacyHeaderDeserialize.class)
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
