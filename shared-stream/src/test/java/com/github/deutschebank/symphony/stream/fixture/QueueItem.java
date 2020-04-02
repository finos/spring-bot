package com.github.deutschebank.symphony.stream.fixture;

import java.util.Objects;

public class QueueItem {

	public final String id;
	public final long timestamp;
	
	public QueueItem(String id, long timestamp) {
		super();
		this.id = id;
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, timestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		QueueItem other = (QueueItem) obj;
		return Objects.equals(id, other.id) && timestamp == other.timestamp;
	}
	
	
	
}
