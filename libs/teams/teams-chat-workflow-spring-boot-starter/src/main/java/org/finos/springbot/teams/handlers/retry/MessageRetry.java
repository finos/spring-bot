package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;

import org.finos.springbot.workflow.response.Response;

public class MessageRetry {

	private Response response;
	private int retryCount;
	private int retryAfter;
	private LocalDateTime retryTime; 
	
	public MessageRetry(Response response, int retryCount, int retryAfter, LocalDateTime retryTime) {
		super();
		this.response = response;
		this.retryCount = retryCount;
		this.retryAfter = retryAfter;
		this.retryTime = retryTime;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getRetryAfter() {
		return retryAfter;
	}

	public void setRetryAfter(int retryAfter) {
		this.retryAfter = retryAfter;
	}

	
	public LocalDateTime getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(LocalDateTime retryTime) {
		this.retryTime = retryTime;
	}

	@Override
	public String toString() {
		return "MessageRetry [response=" + response + ", retryCount=" + retryCount + ", retryAfter=" + retryAfter
				+ ", localDate=" + retryTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((retryTime == null) ? 0 : retryTime.hashCode());
		result = prime * result + ((response == null) ? 0 : response.hashCode());
		result = prime * result + retryAfter;
		result = prime * result + retryCount;
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
		MessageRetry other = (MessageRetry) obj;
		if (retryTime == null) {
			if (other.retryTime != null)
				return false;
		} else if (!retryTime.equals(other.retryTime))
			return false;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		if (retryAfter != other.retryAfter)
			return false;
		if (retryCount != other.retryCount)
			return false;
		return true;
	}
	
	
}
