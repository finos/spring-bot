package com.github.deutschebank.symphony.stream.log;

import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.github.deutschebank.symphony.stream.msg.Participant;

public class SharedLogMessage {
	
	public static final VersionSpace VERSION_SPACE = 
		new VersionSpace(SharedLogMessage.class.getPackage().getName(), "1.0", "1.0");

	Participant participant;
	SharedLogMessageType messageType;

	public SharedLogMessage(Participant p, SharedLogMessageType mt) {
		super();
		this.participant = p;
		this.messageType = mt;
	}

	public SharedLogMessage() {
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

	public SharedLogMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(SharedLogMessageType messageType) {
		this.messageType = messageType;
	}
}