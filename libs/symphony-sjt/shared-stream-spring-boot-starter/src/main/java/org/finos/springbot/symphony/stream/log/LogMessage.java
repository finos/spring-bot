package org.finos.springbot.symphony.stream.log;

import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.workflow.annotations.Work;

@Work(jsonTypeName = "org.finos.symphony.toolkit.stream.logMessage")
public class LogMessage {
	 
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