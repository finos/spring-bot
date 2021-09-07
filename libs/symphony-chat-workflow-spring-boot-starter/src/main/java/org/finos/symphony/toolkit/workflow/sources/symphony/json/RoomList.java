package org.finos.symphony.toolkit.workflow.sources.symphony.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.RoomConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.RoomConverter.RoomFormat;

/**
 * This is used for carrying a set of Chat objects, and works with {@link RoomConverter}'s {@link RoomFormat}.
 * 
 * @author rob@kite9.com
 *
 */
@Work
public class RoomList {
	
	private Collection<Chat> contents;

	public Collection<Chat> getContents() {
		return contents;
	}

	public void setContents(Collection<Chat> contents) {
		this.contents = contents;
	}

	public RoomList() {
		this.contents = new ArrayList<Chat>();
	}

	public RoomList(Collection<Chat> arg0) {
		this.contents = arg0;
	}

	public static RoomList of(Chat... Room) {
		return new RoomList(Arrays.asList(Room));
	}

	public void add(Chat Room) {
		contents.add(Room);
	}

	public int size() {
		return contents.size();
	}

	
}
