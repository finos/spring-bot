package org.finos.symphony.toolkit.stream.cluster.transport;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.ws.rs.core.HttpHeaders;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpMulticaster implements Multicaster {
	
	public static Logger LOG = LoggerFactory.getLogger(HttpMulticaster.class);
	
	protected Set<Participant> knownParticipants = new HashSet<Participant>();
	protected Participant self;
	protected ObjectMapper om;

	public HttpMulticaster(Participant self) {
		super();
		this.self = self;
		this.om = new ObjectMapper();
	}

	@Override
	public void accept(Participant t) {
		if (!self.equals(t)) {
			knownParticipants.add(t);
		}
	}

	@Override
	public void sendAsyncMessage(Participant from, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer) {
		for (Participant participant : knownParticipants) {
			sendMessageTo(participant.getDetails(), cm, responsesConsumer);
		}
	}

	private <R extends ClusterMessage> void sendMessageTo(String url, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer) {
		try {
			URL u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
			con.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			con.setDoOutput(true);
			String json = om.writeValueAsString(cm);
			
			try (OutputStream os = con.getOutputStream()) {
				os.write(json.getBytes());
			}
			
			ClusterMessage response = om.readValue(con.getInputStream(), ClusterMessage.class);
			responsesConsumer.accept(response);
		} catch (Exception e) {
			LOG.error("Couldn't send message to "+url, e);
		}
	}

	@Override
	public int getQuorumSize() {
		return knownParticipants.size() + 1;
	}

}
