package com.github.deutschebank.symphony.stream;

import java.util.Objects;

/**
 * The detail here is an open web connect that you can query the participant's liveness on.
 * 
 * @author robmoffat
 *
 */
public class Participant {

	private String details;
	
	public Participant(String details) {
		super();
		this.details = details;
	}
	
	public Participant() {
		super();
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getDetails() {
		return details;
	}


	@Override
	public int hashCode() {
		return Objects.hash(details);
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
		Participant other = (Participant) obj;
		return Objects.equals(details, other.details);
	}

	@Override
	public String toString() {
		return "Participant [details=" + details + "]";
	}

	

}
