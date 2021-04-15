package org.finos.symphony.toolkit.workflow.sources.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.AbstractNeedsWorkflow;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageList;

public class MessageHistory extends AbstractNeedsWorkflow implements History {

	EntityJsonConverter jsonConverter;
	MessagesApi messageApi;
	SymphonyRooms ru;
	
	public MessageHistory(Workflow wf, EntityJsonConverter jsonConverter, MessagesApi messageApi, SymphonyRooms ru) {
		super(wf);
		this.jsonConverter = jsonConverter;
		this.messageApi = messageApi;
		this.ru = ru;
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, null, null);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 1, null, null);
		return convertToOptionalInstance(type, out);
	}

	protected <X> Optional<X> convertToOptionalInstance(Class<X> type, V4MessageList out) {
		for (V4Message msg : out) {
			X o = getRelevantObject(msg, type);
			if (o != null) {
				return Optional.of((X) o);
			}
		}

		return Optional.empty();
	}
	

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, Addressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, null, t);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 1, null, null);
		return convertToOptionalInstance(type, out);
	}

	@Override
	public List<Object> getFromHistory(Tag t, Addressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, since, t);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 50, null, null);
		
		return out.stream()
				.map(msg -> {
					try {
						return getRelevantObject(msg, Object.class);
					} catch (Exception e1) {
						e1.printStackTrace();
						return null;
					}
				})
				.filter(e -> e != null)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	protected <T> T getRelevantObject(V4Message msg, Class<T> required) {
		EntityJson ej = jsonConverter.readValue(msg.getData());
		Object out = jsonConverter.readWorkflow(ej);
		if (out == null) {
			return null;
		} else if (required == null) {
			return (T) out;
		} else if (required.isAssignableFrom(out.getClass())) {
			return (T) out;
		} else {
			for (Entry<String, Object> ent : ej.entrySet()) {
				if (required.isAssignableFrom(ent.getValue().getClass())) {
					return (T) ent.getValue();
				}
			}
			return null;
		} 
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, since, null);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 50, null, null);
				
		return out.stream()
			.map(msg -> {
				try {
					return getRelevantObject(msg, type);
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			})
			.filter(e -> e != null)
			.collect(Collectors.toList());

	}

	private <X> MessageSearchQuery createMessageSearchQuery(Class<X> type, Addressable address, Instant since, Tag t) {
		MessageSearchQuery msq = new MessageSearchQuery();
		if (address != null) {
			msq.setStreamId(ru.getStreamFor(address));
		}
		
		if (since != null) {
			msq.fromDate(since.toEpochMilli());
		}
		
		if (type != null) {
			msq.setHashtag(TagSupport.formatTag(type));
		} else if (t != null) {
			switch (t.getTagType()) {
			case CASH:
				msq.setCashtag(t.getName());
				break;
			case HASH:
				msq.setHashtag(t.getName());
				break;
			case USER:
				msq.setMention(Long.parseLong(t.getId()));
				break;
			}
		}
		
		return msq;
	}


}
