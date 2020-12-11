package org.finos.symphony.toolkit.stream.cluster.transport;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.ws.rs.core.HttpHeaders;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class HttpMulticaster implements Multicaster {
	
	private Set<Participant> knownParticipants = new HashSet<Participant>();
	private Participant self;

	public HttpMulticaster(Participant self) {
		super();
		this.self = self;
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
		WebClient.create(url)
			.post().bodyValue(cm)
			.accept(MediaType.APPLICATION_JSON)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.retrieve()
			.bodyToMono(ClusterMessage.class)
			.doOnNext(responsesConsumer);
	}

	@Override
	public int getQuorumSize() {
		return knownParticipants.size() + 1;
	}

}
