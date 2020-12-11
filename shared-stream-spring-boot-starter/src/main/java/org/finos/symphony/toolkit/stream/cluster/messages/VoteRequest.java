package org.finos.symphony.toolkit.stream.cluster.messages;

import java.util.Objects;

import org.finos.symphony.toolkit.stream.Participant;

public class VoteRequest implements ClusterMessage {
	
	private long electionNumber;
	private Participant candidate;
	
	public VoteRequest(long electionNumber, Participant candidate) {
		this.electionNumber = electionNumber;
		this.candidate = candidate;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(candidate, electionNumber);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VoteRequest other = (VoteRequest) obj;
		return Objects.equals(candidate, other.candidate) && electionNumber == other.electionNumber;
	}

	@Override
	public String toString() {
		return "VoteRequest [electionNumber=" + electionNumber + ", candidate=" + candidate + "]";
	}

}
