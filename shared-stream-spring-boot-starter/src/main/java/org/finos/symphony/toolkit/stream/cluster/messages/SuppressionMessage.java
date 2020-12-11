package org.finos.symphony.toolkit.stream.cluster.messages;

import java.util.Objects;

import org.finos.symphony.toolkit.stream.Participant;

public class SuppressionMessage implements ClusterMessage {

	private Participant leader;
	private long electionNumber;
	
	public SuppressionMessage() {
		super();
	}

	public SuppressionMessage(Participant leader, long electionNumber) {
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
	public int hashCode() {
		return Objects.hash(electionNumber, leader);
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
		SuppressionMessage other = (SuppressionMessage) obj;
		return electionNumber == other.electionNumber && Objects.equals(leader, other.leader);
	}

	@Override
	public String toString() {
		return "SuppressionMessage [leader=" + leader + ", electionNumber=" + electionNumber + "]";
	}
	
	
}
