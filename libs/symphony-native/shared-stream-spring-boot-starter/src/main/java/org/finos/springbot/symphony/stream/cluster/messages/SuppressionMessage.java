package org.finos.springbot.symphony.stream.cluster.messages;

import java.util.Objects;

import org.finos.springbot.symphony.stream.Participant;

public class SuppressionMessage extends AbstractClusterMessage {

	private Participant leader;
	
	public SuppressionMessage() {
		super();
	}

	public SuppressionMessage(String clusterName, Participant leader) {
		super(clusterName);
		this.leader = leader;
	}

	public Participant getLeader() {
		return leader;
	}

	public void setLeader(Participant leader) {
		this.leader = leader;
	}

	@Override
	public String toString() {
		return "SuppressionMessage [leader=" + leader + ", botName=" + botName
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(leader);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuppressionMessage other = (SuppressionMessage) obj;
		return Objects.equals(leader, other.leader);
	}

	
	
	
}
