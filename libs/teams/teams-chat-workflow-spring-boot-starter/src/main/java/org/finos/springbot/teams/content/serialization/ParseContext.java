package org.finos.springbot.teams.content.serialization;

import java.util.List;

import org.finos.springbot.teams.content.TeamsAddressable;

import com.microsoft.bot.schema.Entity;

public class ParseContext {

	public TeamsAddressable within;
	public List<Entity> entities;
	
	public ParseContext(TeamsAddressable within, List<Entity> entities) {
		this.within = within;
		this.entities = entities;
	}

}
