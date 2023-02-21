package org.finos.springbot.teams.handlers;

import java.time.LocalDateTime;

import org.finos.springbot.workflow.response.Response;

public class MessageRetry {

	private Response response;
	private int retryCount;
	private int retryAfter;
	private LocalDateTime currentTime; 
	
	public MessageRetry(Response response, int retryCount, int retryAfter) {
		super();
		this.response = response;
		this.retryCount = retryCount;
		this.retryAfter = retryAfter;
		currentTime = LocalDateTime.now();
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

	
	public LocalDateTime getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(LocalDateTime currentTime) {
		this.currentTime = currentTime;
	}

	@Override
	public String toString() {
		return "MessageRetry [response=" + response + ", retryCount=" + retryCount + ", retryAfter=" + retryAfter
				+ ", localDate=" + currentTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentTime == null) ? 0 : currentTime.hashCode());
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
		if (currentTime == null) {
			if (other.currentTime != null)
				return false;
		} else if (!currentTime.equals(other.currentTime))
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
