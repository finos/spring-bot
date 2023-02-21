package org.finos.springbot.teams.handlers;

import java.time.LocalDateTime;

import org.finos.springbot.workflow.response.Response;

public class RetryMessageConfig {

	private Response response;
	private int retryCount;
	private int retryAfter;
	private LocalDateTime currentTime; 
	
	public RetryMessageConfig(Response response, int retryCount, int retryAfter) {
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
		return "RetryMessageConfig [response=" + response + ", retryCount=" + retryCount + ", retryAfter=" + retryAfter
				+ ", localDate=" + currentTime + "]";
	}
	
	
}
