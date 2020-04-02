package com.github.deutschebank.symphony.stream.log;

import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.github.deutschebank.symphony.stream.Participant;

public class LogMessage {
	
	public static final VersionSpace VERSION_SPACE = 
		new VersionSpace(LogMessage.class.getPackage().getName(), "1.0", "1.0");

	Participant participant;
	LogMessageType messageType;

	public LogMessage(Participant p, LogMessageType mt) {
		super();
		this.participant = p;
		this.messageType = mt;
	}

	public LogMessage() {
		super();
	}

	@Override
	public String toString() {
		return "Message [p=" + participant + ", mt=" + messageType + "]";
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
}