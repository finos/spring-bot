package com.github.deutschebank.symphony.stream.cluster.messages;

import java.util.Objects;

import com.github.deutschebank.symphony.stream.msg.Participant;

public class VoteResponse implements ClusterMessage {
	
	private long electionNumber;
	private Participant candidate;
	private float votes;
	
	public VoteResponse(long electionNumber, Participant candidate, float votes) {
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
		VoteResponse other = (VoteResponse) obj;
		return Objects.equals(candidate, other.candidate) && electionNumber == other.electionNumber;
	}

	@Override
	public String toString() {
		return "VoteResponse [electionNumber=" + electionNumber + ", candidate=" + candidate + ", votes=" +votes  + "]";
	}


}
