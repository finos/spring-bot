package org.finos.springbot.teams.response.templating;

import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.workflow.response.templating.Markup;

import com.microsoft.bot.schema.Entity;

public class MarkupAndEntities implements Markup {

	private final String content;
	private final List<Entity> entities;
	
	public MarkupAndEntities(String s, List<Entity> e) {
		this.content = s;
		this.entities = e;
	}
	
	public MarkupAndEntities(String content) {
		super();
		this.content = content;
		this.entities = new ArrayList<>();
	}

	public MarkupAndEntities() {
		this(null);
	}

	@Override
	public String getContents() {
		return content;
	}
	

	public List<Entity> getEntities() {
		return entities;
	}
}
