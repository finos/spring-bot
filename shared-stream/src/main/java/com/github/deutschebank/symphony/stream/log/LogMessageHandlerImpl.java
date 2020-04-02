package com.github.deutschebank.symphony.stream.log;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.detuschebank.symphony.json.EntityJson;
import com.github.detuschebank.symphony.json.ObjectMapperFactory;
import com.github.deutschebank.symphony.stream.MessagingVersionSpace;
import com.github.deutschebank.symphony.stream.SharedStreamException;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;

public class LogMessageHandlerImpl implements LogMessageHandler {

	private static final String SHARED_STREAM_JSON_KEY = "shared-stream-001";
	private ObjectMapper om;
	private String environmentSuffix;
	private String streamId;
	protected MessagesApi messagesApi;
	
	public LogMessageHandlerImpl(String streamId, MessagesApi messagesApi, String environmentSuffix) {
		super();
		this.streamId = streamId;
		this.om = ObjectMapperFactory.initialize(
			ObjectMapperFactory.extendedSymphonyVersionSpace(MessagingVersionSpace.THIS));
		this.messagesApi = messagesApi;
		this.environmentSuffix = environmentSuffix == null ? "prod" : environmentSuffix;
	}

	public void writeLogMessage(LogMessage out) {
		String messageML = createMessageML(out);
		String jsonStr = serializeJson(out);
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, messageML, jsonStr, null, null, null, null);
	}

	public String createMessageML(LogMessage out) {
		return "<messageML>"
				+getHashTag(out.getMessageType())
				+out.getMessageType()
				+" - "
				+out.getParticipant().getDetails()
				+"</messageML>"; 
	}

	public String getHashTagId(LogMessageType cmt) {
		return "shared-stream-"+cmt.toString().toLowerCase()+"-"+environmentSuffix;
	}

	public String getHashTag(LogMessageType t) {
		return "<hash tag=\""+getHashTagId(t)+"\" /> ";
	}
	
	public String serializeJson(LogMessage out) {
		try {
			EntityJson json = new EntityJson();
			json.put(SHARED_STREAM_JSON_KEY, out);
			return om.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			throw new SharedStreamException(e);
		}
	}

	@Override
	public boolean isLeaderMessage(V4Event e) {
		V4MessageSent messageSent = e.getPayload().getMessageSent();
		if (messageSent != null) {
			V4Message m = messageSent.getMessage();
			String messageML = m.getMessage();
			return (messageML.contains(getHashTag(LogMessageType.LEADER)));
		}
		
		return false;
	}

	@Override
	public Optional<LogMessage> readMessage(V4Message e) {
		String json = e.getData();
		LogMessage o = deserializeJson(json);
		return Optional.of(o);
	}

	public LogMessage deserializeJson(String json) {
		try {
			EntityJson ej = om.readValue(json, EntityJson.class);
			LogMessage o = (LogMessage) ej.get(SHARED_STREAM_JSON_KEY);
			return o;
		} catch (Exception e) {
			throw new SharedStreamException(e);
		}
	}

	public String getStreamId() {
		return streamId;
	}

}
