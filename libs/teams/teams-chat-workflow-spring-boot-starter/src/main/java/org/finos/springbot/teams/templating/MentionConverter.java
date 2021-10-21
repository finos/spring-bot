package org.finos.springbot.teams.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class MentionConverter extends TextFieldConverter<JsonNode> {

	final String location;
	
	public MentionConverter() {
		this("entity.rooms");
	}
	
	public MentionConverter(String location) {
		super(LOW_PRIORITY, TeamsChat.class);
		this.location = location;
	}
	
	private static final RoomFormat ROOM_FORMAT = new RoomFormat();

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable v) {
		if (editMode) {
			return renderDropdown(v, location, ROOM_FORMAT);
		} else {
			return "${" + ROOM_FORMAT.getValueFunction().apply(v.getDataPath(), location) +  "!''}";
		}
	}
	
}
