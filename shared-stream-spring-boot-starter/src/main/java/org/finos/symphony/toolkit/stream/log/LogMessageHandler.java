package org.finos.symphony.toolkit.stream.log;

import java.util.Optional;

import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;

/**
 * Handles read/write of {@link LogMessage}s, which generally will go to a symphony room and
 * be read from a symphony stream.
 * 
 * @author robmoffat
 *
 */
public interface LogMessageHandler {
	
	public void writeLogMessage(LogMessage slm);
	
	public boolean isLeaderMessage(V4Event e);

	public boolean isParticipantMessage(V4Event e);
	
	/**
	 * Converts the {@link V4Message} into a {@link LogMessage} if it is possible.
	 */
	public Optional<LogMessage> readMessage(V4Message e);

}
