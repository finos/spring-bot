package org.finos.symphony.toolkit.stream.log;

import org.finos.springbot.entityjson.VersionSpace;
import org.finos.symphony.toolkit.stream.Participant;

public class LogMessage {
	
	public static final VersionSpace VERSION_SPACE = new VersionSpace(LogMessage.class, "1.0", "1.0");

	String cluster;
	Participant participant;
	LogMessageType messageType;

	public LogMessage(String cluster, Participant p, LogMessageType mt) {
		super();
		this.cluster = cluster;
		this.participant = p;
		this.messageType = mt;
	}

	public LogMessage() {
		super();
	}

	@Override
	public String toString() {
		return "LogMessage [cluster=" + cluster + ", participant=" + participant + ", messageType=" + messageType + "]";
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public LogMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(LogMessageType messageType) {
		this.messageType = messageType;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
}