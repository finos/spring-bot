package org.finos.symphony.toolkit.stream.log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.stream.Participant;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;

/**
 * Implements the shared log using a symphony stream.  Entries in the log prior to 24 hours ago 
 * are ignored.
 * 
 * @author robmoffat
 *
 */
public class SymphonyRoomSharedLog extends LogMessageHandlerImpl implements SharedLog  {
	
	public static final long ONE_HOUR = 1000 * 60 * 60;
	
	private long participationIntervalMillis;
	
	public SymphonyRoomSharedLog(String streamId, MessagesApi messagesApi, String environmentSuffix, long participationIntervalMillis) {
		super(streamId, messagesApi, environmentSuffix);
		this.participationIntervalMillis = participationIntervalMillis;
	}

	@Override
	public void writeLeaderMessage(Participant p) {
		LogMessage out = new LogMessage(p, LogMessageType.LEADER);
		writeLogMessage(out);
	}

	@Override
	public void writeParticipantMessage(Participant p) {
		LogMessage out = new LogMessage(p, LogMessageType.PARTICIPANT);
		writeLogMessage(out);
	}

	@Override
	public List<Participant> getRegisteredParticipants(Participant p) {
		return performQuery(LogMessageType.PARTICIPANT, 1000);		
	}

	protected List<Participant> performQuery(LogMessageType messageType, int count) {
		long since = System.currentTimeMillis() - participationIntervalMillis - ONE_HOUR;
		MessageSearchQuery msq = new MessageSearchQuery()
			.hashtag(getHashTagId(messageType))
			.streamId(getStreamId())
			.fromDate(since)
			.streamType("ROOM");
		return messagesApi.v1MessageSearchPost(msq, null, null, 0, count, null, null).stream()
			.map(m -> readMessage(m))
			.filter(o -> o.isPresent())
			.map(o -> o.get())
			.map(cm -> cm.getParticipant())
			.distinct()
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Participant> getLeader(Participant p) {
		return performQuery(LogMessageType.LEADER, 1).stream().findFirst();
	}

}
