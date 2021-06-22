package org.finos.symphony.toolkit.stream;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.stream")
public class SharedStreamProperties {

	public static enum EndpointScheme { HTTP, HTTPS };
	
	private EndpointScheme endpointScheme = EndpointScheme.HTTP;
	private String coordinationStreamId;

	private String environmentIdentifier = "test";
	private long participantWriteIntervalMillis = 24*60*60*1000;	// one day by default.
	private String endpointPath = "/symphony-api/cluster-communication";
	private String endpointHostAndPort = null;
	private long timeoutMs = 5000;
	private boolean startImmediately = true;
	

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

	public String getEndpointPath() {
		return endpointPath;
	}

	public void setEndpointPath(String endpointPath) {
		this.endpointPath = endpointPath;
	}

	public String getEndpointHostAndPort() {
		return endpointHostAndPort;
	}

	public void setEndpointHostAndPort(String endpointHostAndPort) {
		this.endpointHostAndPort = endpointHostAndPort;
	}

	public long getTimeoutMs() {
		return timeoutMs;
	}

	public void setTimeoutMs(long timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public boolean isStartImmediately() {
		return startImmediately;
	}

	public void setStartImmediately(boolean startImmediately) {
		this.startImmediately = startImmediately;
	}

	public EndpointScheme getEndpointScheme() {
		return endpointScheme;
	}

	public void setEndpointScheme(EndpointScheme endpointScheme) {
		this.endpointScheme = endpointScheme;
	}

}
