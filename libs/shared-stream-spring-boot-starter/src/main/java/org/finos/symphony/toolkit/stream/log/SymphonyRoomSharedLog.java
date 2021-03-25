package org.finos.symphony.toolkit.stream.log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.stream.Participant;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;
import com.symphony.api.model.V4Event;

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
	private Participant cachedLastKnownLeader;
	private List<Participant> cachedParticipants;
	
	public SymphonyRoomSharedLog(String clusterName, String streamId, MessagesApi messagesApi, String environmentSuffix, long participationIntervalMillis) {
		super(clusterName, streamId, messagesApi, environmentSuffix);
		this.participationIntervalMillis = participationIntervalMillis;
	}

	@Override
	public void writeLeaderMessage(Participant p) {
		LogMessage out = new LogMessage(clusterName, p, LogMessageType.LEADER);
		writeLogMessage(out);
	}

	@Override
	public void writeParticipantMessage(Participant p) {
		LogMessage out = new LogMessage(clusterName, p, LogMessageType.PARTICIPANT);
		writeLogMessage(out);
	}

	@Override
	public List<Participant> getRecentParticipants() {
		if (cachedParticipants == null) {
			cachedParticipants = performQuery(LogMessageType.PARTICIPANT, 1000);	
			LOG.debug("Cluster {} participants: {}", clusterName, cachedParticipants);
		} 
		
		return cachedParticipants;
	}

	protected List<Participant> performQuery(LogMessageType messageType, int count) {
		long since = System.currentTimeMillis() - participationIntervalMillis - ONE_HOUR;
		MessageSearchQuery msq = new MessageSearchQuery()
			.hashtag(getHashTagId())
			.streamId(getStreamId())
			.fromDate(since)
			.streamType("ROOM");
		return messagesApi.v1MessageSearchPost(msq, null, null, 0, count, null, null).stream()
			.map(m -> readMessage(m))
			.filter(o -> o.isPresent())
			.map(o -> o.get())
			.filter(cm -> cm.messageType == messageType)
			.map(cm -> cm.getParticipant())
			.distinct()
			.collect(Collectors.toList());
	}

	@Override
	public void becomeLeader(Participant cm) {
		writeLeaderMessage(cm);
	}

	@Override
	public boolean isLeader(Participant cm) {
		if (cachedLastKnownLeader == null) {
			cachedLastKnownLeader = getLastRecordedLeader(cm).orElse(null);
		}
		
		return cm.equals(cachedLastKnownLeader);
	}

	@Override
	public Optional<Participant> getLastRecordedLeader(Participant me) {
		Optional<Participant> out = performQuery(LogMessageType.LEADER, 1).stream().findFirst();
		LOG.debug("Cluster {} leader: {}", clusterName, out);
		return out;
	}

	
	@Override
	public Optional<LogMessage> handleEvent(V4Event e) {
		Optional<LogMessage> out = super.handleEvent(e);
		
		if (out.isPresent()) {
			LogMessage logMessage = out.get();
			Participant newParticipant = logMessage.participant;
			if (logMessage.messageType == LogMessageType.LEADER) {
				cachedLastKnownLeader = newParticipant;
			}
			
			List<Participant> allKnownParticipants = getRecentParticipants();
			if (!allKnownParticipants.contains(newParticipant)) {
				allKnownParticipants.add(newParticipant);
			}
		}
	
		return out;
	}
	
	

}
