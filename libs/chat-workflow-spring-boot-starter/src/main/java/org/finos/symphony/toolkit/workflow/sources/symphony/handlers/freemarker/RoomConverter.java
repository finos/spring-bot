package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.workflow.content.Room;

public class RoomConverter extends AbstractClassConverter {

	public RoomConverter() {
		super(LOW_PRIORITY, Room.class);
	}
	
	@Override
	public String apply(Type t, boolean editMode, Variable v) {
		
		if (editMode) {
			StringBuilder out = new StringBuilder();
			out.append(indent(v.depth) + "<select "+ attribute(v, "name", v.getFormFieldName()));
			out.append(attribute(v, "required", "false"));
			out.append(attribute(v, "data-placeholder", "Choose "+v.getDisplayName()));
			out.append(">");
			out.append(indent(v.depth) + "<#list entity.rooms as r>");
			out.append(indent(v.depth) + "<option ");
			out.append(attribute(v, "value", "hi"));
			out.append(attributeParam(v, "selected", "bingo"));
			out.append(indent(v.depth) + ">");
			out.append("</option>");
			out.append("</select>");
			return out.toString();
		} else {
			return text(v, "!''");
		}
	}
	
}
