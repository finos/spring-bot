package org.finos.symphony.toolkit.stream.web;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import javax.ws.rs.core.HttpHeaders;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple implementation of the multicaster, which uses Http to talk with {@link HttpClusterMessageController}.
 * 
 * @author rob@kite9.com
 *
 */
public class HttpMulticaster implements Multicaster {
	
	public static Logger LOG = LoggerFactory.getLogger(HttpMulticaster.class);
	
	protected Participant self;
	protected ObjectMapper om;

	public HttpMulticaster(Participant self) {
		super();
		this.self = self;
		this.om = new ObjectMapper();
	}

	@Override
	public void sendAsyncMessage(Participant from, List<Participant> to, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer) {
		to.stream()
			.filter(participant -> !participant.equals(from))
			.forEach(participant ->  sendMessageTo(participant.getDetails(), cm, responsesConsumer));
	}

	private <R extends ClusterMessage> void sendMessageTo(String url, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer) {
		try {
			String json = om.writeValueAsString(cm);
			LOG.debug("Sending Cluster Communication Message {} to {}", json, url);
			
			URL u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
			con.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			con.setDoOutput(true);
			
			try (OutputStream os = con.getOutputStream()) {
				os.write(json.getBytes());
			}
			
			ClusterMessage response = om.readValue(con.getInputStream(), ClusterMessage.class);

			LOG.debug("Received Response {}", response);
			responsesConsumer.accept(response);
		} catch (Exception e) {
			LOG.debug("Couldn't send message to {}, {}", url, e.getMessage());
		}
	}

}
