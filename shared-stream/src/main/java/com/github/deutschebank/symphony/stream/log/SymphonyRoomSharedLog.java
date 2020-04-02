package com.github.deutschebank.symphony.stream.log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.detuschebank.symphony.json.EntityJson;
import com.github.detuschebank.symphony.json.ObjectMapperFactory;
import com.github.deutschebank.symphony.stream.MessagingVersionSpace;
import com.github.deutschebank.symphony.stream.SharedStreamException;
import com.github.deutschebank.symphony.stream.msg.Participant;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;

/**
 * Implements the shared log using a symphony stream.  Entries in the log prior to 24 hours ago 
 * are ignored.
 * 
 * @author robmoffat
 *
 */
public class SymphonyRoomSharedLog implements SharedLog {

	private static final String SHARED_STREAM_JSON_KEY = "shared-stream-001";

	private String streamId;
	
	private ObjectMapper om;
	
	private MessagesApi messagesApi;
	
	private String environmentSuffix;
	
	public SymphonyRoomSharedLog(String streamId, MessagesApi messagesApi, String environmentSuffix) {
		super();
		this.streamId = streamId;
		this.om = ObjectMapperFactory.initialize(
			ObjectMapperFactory.extendedSymphonyVersionSpace(MessagingVersionSpace.THIS));
		this.messagesApi = messagesApi;
		this.environmentSuffix = environmentSuffix == null ? "prod" : environmentSuffix;
	}

	@Override
	public void writeLeaderMessage(Participant p) {
		SharedLogMessage out = new SharedLogMessage(p, SharedLogMessageType.LEADER);
		writeMessage(out);
	}

	protected void writeMessage(SharedLogMessage out) {
		String messageML = createMessageML(out);
		EntityJson json = new EntityJson();
		json.put(SHARED_STREAM_JSON_KEY, out);
		String jsonStr = serializeJson(json);
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, messageML, jsonStr, null, null, null, null);
	}

	private String createMessageML(SharedLogMessage out) {
		return "<messageML>"
				+"<hash tag=\""+getHashTagId(out.getMessageType())+"\" /> "
				+out.getMessageType()
				+" - "
				+out.getParticipant().getDetails()
				+"</messageML>"; 
	}

	@Override
	public void writeParticipantMessage(Participant p) {
		SharedLogMessage out = new SharedLogMessage(p, SharedLogMessageType.PARTICIPANT);
		writeMessage(out);
	}

	@Override
	public List<Participant> getRegisteredParticipants(Participant p) {
		return performQuery(SharedLogMessageType.PARTICIPANT, 1000);		
	}

	protected List<Participant> performQuery(SharedLogMessageType messageType, int count) {
		long last24Hours = System.currentTimeMillis() - (24*60*60*1000);
		MessageSearchQuery msq = new MessageSearchQuery()
			.hashtag(getHashTagId(messageType))
			.streamId(streamId)
			.fromDate(last24Hours)
			.streamType("ROOM");
		return messagesApi.v1MessageSearchPost(msq, null, null, 0, count, null, null).stream()
			.map(m -> m.getData())
			.map(json -> deserializeJson(json))
			.map(ej -> (SharedLogMessage) ej.get(SHARED_STREAM_JSON_KEY))
			.map(cm -> cm.getParticipant())
			.distinct()
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Participant> getLeader(Participant p) {
		return performQuery(SharedLogMessageType.LEADER, 1).stream().findFirst();
	}
	
	protected String serializeJson(EntityJson json) {
		try {
			return om.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			throw new SharedStreamException(e);
		}
	}
	
	protected EntityJson deserializeJson(String json) {
		try {
			return om.readValue(json, EntityJson.class);
		} catch (Exception e) {
			throw new SharedStreamException(e);
		}
	}
	
	protected String getHashTagId(SharedLogMessageType cmt) {
		return "shared-stream-"+cmt.toString().toLowerCase()+"-"+environmentSuffix;
	}

}
