package org.finos.symphony.toolkit.stream.cluster.messages;

import java.util.Objects;

import org.finos.symphony.toolkit.stream.Participant;

public class VoteResponse extends AbstractClusterMessage {
	
	private long electionNumber;
	private Participant candidate;
	private float votes;
	
	public VoteResponse() {
	}
	
	public VoteResponse(String clusterName, long electionNumber, Participant candidate, float votes) {
		super(clusterName);
		this.electionNumber = electionNumber;
		this.candidate = candidate;
		this.votes = votes;
	}
	
	public long getElectionNumber() {
		return electionNumber;
	}
	
	public void setElectionNumber(long electionNumber) {
		this.electionNumber = electionNumber;
	}
	
	public Participant getCandidate() {
		return candidate;
	}
	
	public void setCandidate(Participant candidate) {
		this.candidate = candidate;
	}
	
	public float getVotes() {
		return votes;
	}

	public void setVotes(float votes) {
		this.votes = votes;
	}

	@Override
	public String toString() {
		return "VoteResponse [electionNumber=" + electionNumber + ", candidate=" + candidate + ", votes=" + votes
				+ ", botName=" + botName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(candidate, electionNumber, votes);
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
		VoteResponse other = (VoteResponse) obj;
		return Objects.equals(candidate, other.candidate) && electionNumber == other.electionNumber
				&& Float.floatToIntBits(votes) == Float.floatToIntBits(other.votes);
	}
	
	

}
