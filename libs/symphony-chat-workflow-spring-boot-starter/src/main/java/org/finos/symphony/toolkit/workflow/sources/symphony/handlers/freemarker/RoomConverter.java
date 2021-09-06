package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.finos.symphony.toolkit.workflow.content.Chat;

public class RoomConverter extends AbstractDropdownConverter {

	final String location;
	
	public RoomConverter() {
		this("entity.rooms");
	}
	
	public RoomConverter(String location) {
		super(LOW_PRIORITY, Chat.class);
		this.location = location;
	}

	public static class RoomFormat implements ElementFormat {

		@Override
		public Function<String, String> getKeyFunction() {
			return (k) -> k+".id[0].value";
		}

		@Override
		public BiFunction<String, String, String> getValueFunction() {
			return (k, d) -> k+".id[1].value";
		}

		@Override
		public Function<String, String> getSourceFunction() {
			return (k) -> k+ "[1]";
		}
	};
	
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
