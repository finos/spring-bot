package org.finos.symphony.toolkit.workflow.content;

import java.util.TimeZone;

public class RoomDef implements Room {

	protected String roomName;
	protected String roomDescription;
	protected boolean pub;
	protected String id;

	public RoomDef() {
	}
	
	public RoomDef(String name, String description, boolean pub, String id) {
		super();
		this.roomName = name;
		this.roomDescription = description;
		this.pub = pub;
		this.id = id;
	}

	@Override
	public String getRoomName() {
		return roomName;
	}

	@Override
	public String getRoomDescription() {
		return roomDescription;
	}

	@Override
	public boolean isPub() {
		return pub;
	}


	@Override
	public String toString() {
		return "RoomDef [name=" + getRoomName() + ", description=" + getRoomDescription() + ", pub=" + isPub() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roomName == null) ? 0 : roomName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomDef other = (RoomDef) obj;
		if (roomName == null) {
			if (other.roomName != null)
				return false;
		} else if (!roomName.equals(other.roomName))
			return false;
		return true;
	}

	public String getId() {
		return id;
	}




}
