package org.finos.symphony.toolkit.stream.cluster.messages;

import org.finos.symphony.toolkit.stream.Participant;

public class VoteRequest implements ClusterMessage {
	
	private long electionNumber;
	private Participant candidate;
	
	public VoteRequest() {
	}
	
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
		int result = 1;
		result = prime * result + ((candidate == null) ? 0 : candidate.hashCode());
		result = prime * result + (int) (electionNumber ^ (electionNumber >>> 32));
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
		VoteRequest other = (VoteRequest) obj;
		if (candidate == null) {
			if (other.candidate != null)
				return false;
		} else if (!candidate.equals(other.candidate))
			return false;
		if (electionNumber != other.electionNumber)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VoteRequest [electionNumber=" + electionNumber + ", candidate=" + candidate + "]";
	}

}
