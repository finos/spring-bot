package org.finos.symphony.toolkit.workflow.sources.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.CashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.streams.AbstractStreamResolving;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageList;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

public class SymphonyHistoryImpl extends AbstractStreamResolving implements SymphonyHistory {

	EntityJsonConverter jsonConverter;
	MessagesApi messageApi;
	
	public SymphonyHistoryImpl(EntityJsonConverter jsonConverter, MessagesApi messageApi, StreamsApi streamsApi, UsersApi usersApi) {
		super(streamsApi, usersApi);
		this.jsonConverter = jsonConverter;
		this.messageApi = messageApi;
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
		return getRelevantObject(getLastEntityJsonFromHistory(type, address), type); 
	}
	
	@Override
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Addressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, null, null);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 1, null, null);
		return convertToOptionalEntityJson(out);
	}

	protected Optional<EntityJson> convertToOptionalEntityJson(V4MessageList out) {
		for (V4Message msg : out) {
			EntityJson o = getEntityJson(msg);
			if (o != null) {
				return Optional.of(o);
			}
		}

		return Optional.empty();
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
		return getRelevantObject(getLastEntityJsonFromHistory(type, t, address), type);
	}


	@Override
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Tag t, Addressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, null, t);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 1, null, null);
		return convertToOptionalEntityJson(out);
	}
	
	@Override
	public <X> List<X> getFromHistory(Class<X> type, Tag t, Addressable address, Instant since) {
		return getFromEntityJson(getEntityJsonFromHistory(t, address, since), type);
	}
	

	@Override
	public <X> List<X> getFromEntityJson(List<EntityJson> ej, Class<X> type) {
		return ej.stream()
		.map(i -> getRelevantObject(Optional.of(i), type))
		.filter(o -> o.isPresent())
		.map(o -> o.get())
		.collect(Collectors.toList());
	}

	
	@Override
	public List<EntityJson> getEntityJsonFromHistory(Tag t, Addressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, since, t);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 50, null, null);
		return out.stream()
				.map(msg -> getEntityJson(msg))
				.filter(e -> e != null)
				.collect(Collectors.toList());
	}

	protected EntityJson getEntityJson(V4Message msg) {
		try {
			return jsonConverter.readValue(msg.getData());
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	protected <T> T getRelevantObject(V4Message msg, Class<T> required) {
		EntityJson ej = getEntityJson(msg);
		return getFromEntityJson(ej, required).orElse(null);
	}
	
	protected <T> Optional<T> getRelevantObject(Optional<EntityJson> ej, Class<T> required) {
		if ((ej == null) || (!ej.isPresent())) {
			return Optional.empty();
		}
		
		return getFromEntityJson(ej.get(), required);	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> Optional<X> getFromEntityJson(EntityJson ej, Class<X> required) {
		for (Entry<String, Object> ent : ej.entrySet()) {
			if (required.isAssignableFrom(ent.getValue().getClass())) {
				return Optional.of((X) ent.getValue());
			}
		}
		
		return Optional.empty();		
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
		return getEntityJsonFromHistory(type, address, since).stream()
				.map(ej -> getRelevantObject(Optional.of(ej), type))
				.filter(o -> o.isPresent())
				.map(o -> o.get())
				.collect(Collectors.toList());
	}
	

	@Override
	public <X> List<EntityJson> getEntityJsonFromHistory(Class<X> type, Addressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, since, null);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 50, null, null);
		return out.stream()
				.map(msg -> getEntityJson(msg))
				.filter(e -> e != null)
				.collect(Collectors.toList());
	}
	
	

	private <X> MessageSearchQuery createMessageSearchQuery(Class<X> type, Addressable address, Instant since, Tag t) {
		MessageSearchQuery msq = new MessageSearchQuery();
		if (address instanceof SymphonyAddressable) {
			msq.setStreamId(getStreamFor((SymphonyAddressable) address));
		}
		
		if (since != null) {
			msq.fromDate(since.toEpochMilli());
		}
		
		if (type != null) {
			msq.setHashtag(TagSupport.formatTag(type));
		} else if (t != null) {
			if (t instanceof CashTag) {
				msq.setCashtag(t.getName());
			} else if (t instanceof HashTag) {
				msq.setHashtag(t.getName());
			} else if (t instanceof SymphonyUser) {
				msq.setMention(Long.parseLong(((SymphonyUser)t).getUserId()));
			}
		}
		
		return msq;
	}

	

	

}
