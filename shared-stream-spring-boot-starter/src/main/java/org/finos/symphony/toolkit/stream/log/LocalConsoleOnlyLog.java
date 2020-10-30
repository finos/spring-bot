package org.finos.symphony.toolkit.stream.log;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.stream.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;

/**
 * This implementation is used in the event that the user hasn't declared a coordination room in their 
 * application.yml.  
 * 
 * @author moffrob
 */
		
public class LocalConsoleOnlyLog implements SharedLog, LogMessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(LocalConsoleOnlyLog.class);

	
	public LocalConsoleOnlyLog() {
		LOG.error("Clustering is disabled - don't deploy like this on multiple instances. Cluster instances are therefore referred to as 'dummy' below. ");
		LOG.error("Please create a coordination room with this bot as member, and configure the symphony.stream.coordination-stream-id property to fix.");
	}
	
	@Override
	public void writeLeaderMessage(Participant p) {
		LOG.warn("Made Leader: {}", p.getDetails());
	}

	@Override
	public void writeParticipantMessage(Participant p) {
		LOG.warn("Participant: {} ",p.getDetails());
	}

	@Override
	public List<Participant> getRegisteredParticipants(Participant me) {
		return Collections.singletonList(me);
	}

	@Override
	public Optional<Participant> getLeader(Participant me) {
		return Optional.of(me);
	}

	@Override
	public void writeLogMessage(LogMessage slm) {
		LOG.warn("Log Message: {}", slm.messageType);
	}

	@Override
	public boolean isLeaderMessage(V4Event e) {
		return false;
	}

	@Override
	public boolean isParticipantMessage(V4Event e) {
		return false;
	}

	@Override
	public Optional<LogMessage> readMessage(V4Message e) {
		return Optional.empty();
	}
	
	
	
	
}