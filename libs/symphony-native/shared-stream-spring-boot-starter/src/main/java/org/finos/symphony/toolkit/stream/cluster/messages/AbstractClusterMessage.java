package org.finos.symphony.toolkit.stream.cluster.messages;

import java.util.Objects;

public abstract class AbstractClusterMessage implements ClusterMessage {

	protected String botName;
	
	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public AbstractClusterMessage() {
		super();
	}

	public AbstractClusterMessage(String botName) {
		super();
		this.botName = botName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(botName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractClusterMessage other = (AbstractClusterMessage) obj;
		return Objects.equals(botName, other.botName);
	}
	
	
}
