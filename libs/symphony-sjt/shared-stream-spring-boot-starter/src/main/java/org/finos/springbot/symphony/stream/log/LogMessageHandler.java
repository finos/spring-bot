package org.finos.springbot.symphony.stream.log;

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

	/**
	 * Handles the event, returning a LogMessage if the event refers to one of those.
	 */
	public Optional<LogMessage> handleEvent(V4Event e);


	/**
	 * Converts the {@link V4Message} into a {@link LogMessage} if it is possible.
	 */
	public Optional<LogMessage> readMessage(V4Message e);


}
