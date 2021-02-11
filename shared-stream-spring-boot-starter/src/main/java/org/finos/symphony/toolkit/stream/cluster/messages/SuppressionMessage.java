package org.finos.symphony.toolkit.stream.cluster.messages;

import java.util.Objects;

import org.finos.symphony.toolkit.stream.Participant;

public class SuppressionMessage extends AbstractClusterMessage {

	private Participant leader;
	private long electionNumber;
	
	public SuppressionMessage() {
		super();
	}

	public SuppressionMessage(String clusterName, Participant leader, long electionNumber) {
		super(clusterName);
		this.leader = leader;
		this.electionNumber = electionNumber;
	}

	public Participant getLeader() {
		return leader;
	}

	public void setLeader(Participant leader) {
		this.leader = leader;
	}


	public long getElectionNumber() {
		return electionNumber;
	}

	public void setElectionNumber(long electionNumber) {
		this.electionNumber = electionNumber;
	}

	@Override
	public String toString() {
		return "SuppressionMessage [leader=" + leader + ", electionNumber=" + electionNumber + ", botName=" + botName
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(electionNumber, leader);
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
		return electionNumber == other.electionNumber && Objects.equals(leader, other.leader);
	}

	
	
	
}
