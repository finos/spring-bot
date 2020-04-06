package com.github.deutschebank.symphony.stream.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.stream")
public class SymphonyStreamProperties {

	static enum Algorithm { MAJORITY, BULLY };
	
	private String coordinationStreamId;
	private String environmentIdentifier;
	private long participantWriteIntervalMillis = 24*60*60*1000;	// one day by default.
	private Algorithm algorithm = Algorithm.BULLY;
	private String endpointPath = "/symphony-api/cluster-communication";
	private String internalHostUrl = null;
	private long timeoutMs = 5000;
	

	public String getCoordinationStreamId() {
		return coordinationStreamId;
	}

	public void setCoordinationStreamId(String coordinationStreamId) {
		this.coordinationStreamId = coordinationStreamId;
	}

	public String getEnvironmentIdentifier() {
		return environmentIdentifier;
	}

	public void setEnvironmentIdentifier(String environmentIdentifier) {
		this.environmentIdentifier = environmentIdentifier;
	}

	public long getParticipantWriteIntervalMillis() {
		return participantWriteIntervalMillis;
	}

	public void setParticipantWriteIntervalMillis(long participantWriteIntervalMillis) {
		this.participantWriteIntervalMillis = participantWriteIntervalMillis;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String getEndpointPath() {
		return endpointPath;
	}

	public void setEndpointPath(String endpointPath) {
		this.endpointPath = endpointPath;
	}

	public String getInternalHostUrl() {
		return internalHostUrl;
	}

	public void setInternalHostUrl(String internalHostUrl) {
		this.internalHostUrl = internalHostUrl;
	}

	public long getTimeoutMs() {
		return timeoutMs;
	}

	public void setTimeoutMs(long timeoutMs) {
		this.timeoutMs = timeoutMs;
	}
}
