package com.github.deutschebank.symphony.workflow.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.deutschebank.symphony.workflow.content.Room;

public class RoomList {
	
	private Collection<Room> contents;

	public Collection<Room> getContents() {
		return contents;
	}

	public void setContents(Collection<Room> contents) {
		this.contents = contents;
	}

	public RoomList() {
		this.contents = new ArrayList<Room>();
	}

	public RoomList(Collection<Room> arg0) {
		this.contents = arg0;
	}

	public static RoomList of(Room... Room) {
		return new RoomList(Arrays.asList(Room));
	}

	public void add(Room Room) {
		contents.add(Room);
	}

	public int size() {
		return contents.size();
	}

	
}
