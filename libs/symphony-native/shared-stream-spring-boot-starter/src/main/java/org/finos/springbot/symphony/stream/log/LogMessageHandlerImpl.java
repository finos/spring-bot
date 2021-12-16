package org.finos.springbot.symphony.stream.log;

import java.util.Optional;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.stream.SharedStreamException;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;

public class LogMessageHandlerImpl implements LogMessageHandler {

	private static final String SYMPHONY_SHARED_STREAM = "symphony-shared-stream";
	private static final String SHARED_STREAM_JSON_KEY = "shared-stream-001";
	protected static final Logger LOG = LoggerFactory.getLogger(LogMessageHandlerImpl.class);
	protected EntityJsonConverter ejc;
	protected String environmentSuffix;
	protected String streamId;
	protected String clusterName;
	protected MessagesApi messagesApi;
	
	public LogMessageHandlerImpl(String clusterName, String streamId, MessagesApi messagesApi, String environmentSuffix, EntityJsonConverter ejc) {
		super();
		this.clusterName = clusterName;
		this.streamId = streamId;
		this.ejc = ejc;
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
				+"<hash tag=\""+SYMPHONY_SHARED_STREAM+"\" />"
				+getHashTag()
				+out.getMessageType()
				+" - "
				+out.getParticipant().getDetails()
				+"</messageML>"; 
	}

	public String getHashTagId() {
		String normd = clusterName.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		return "shared-stream-"+normd+"-"+environmentSuffix;
	}

	public String getHashTag() {
		return "<hash tag=\""+getHashTagId()+"\" /> ";
	}
	
	public String serializeJson(LogMessage out) {
		EntityJson json = new EntityJson();
		json.put(SHARED_STREAM_JSON_KEY, out);
		return ejc.writeValue(json);
	}

	@Override
	public Optional<LogMessage> handleEvent(V4Event e) {
		V4MessageSent messageSent = e.getPayload().getMessageSent();
		if (messageSent != null) {
			V4Message m = messageSent.getMessage();
			String messageML = m.getMessage();
			if  (messageML.contains(getHashTagId())) {
				Optional<LogMessage> lm = readMessage(m);
				if ((lm.isPresent()) && (lm.get().getCluster().equals(clusterName))) {
					return lm;
				}
			}
			
		}
		
		return Optional.empty();
	}

	@Override
	public Optional<LogMessage> readMessage(V4Message e) {
		String json = e.getData();
		try {
			LogMessage o = deserializeJson(json);
			return Optional.of(o);
		} catch (Exception e1) {
			LOG.error("Couldn't deserialize: {}", json, e);
			return Optional.empty();
		}
	}

	public LogMessage deserializeJson(String json) {
		try {
			EntityJson ej = ejc.readValue(json);
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
