package org.finos.springbot.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.content.SymphonyAddressable;
import org.finos.springbot.symphony.conversations.StreamResolver;
import org.finos.springbot.symphony.tags.SymphonyTagSupport;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.data.EntityJsonConverter;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.MessageSearchQuery;
import com.symphony.bdk.gen.api.model.V4Message;

public class SymphonyHistoryImpl implements SymphonyHistory {

	private EntityJsonConverter jsonConverter;
	private MessageService messageApi;
	private StreamResolver sr;

	public SymphonyHistoryImpl(EntityJsonConverter jsonConverter, MessageService messageApi, StreamResolver sr) {
		this.jsonConverter = jsonConverter;
		this.messageApi = messageApi;
		this.sr = sr;
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, SymphonyAddressable address) {
		return getRelevantObject(getLastEntityJsonFromHistory(type, address), type); 
	}

	@Override
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, SymphonyAddressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, null, null);
		PaginationAttribute pa = new PaginationAttribute(0, 1);
		List<V4Message> out = messageApi.searchMessages(msq, pa);
		return convertToOptionalEntityJson(out);
	}

	protected Optional<EntityJson> convertToOptionalEntityJson(List<V4Message> out) {
		for (V4Message msg : out) {
			EntityJson o = getEntityJson(msg);
			if (o != null) {
				return Optional.of(o);
			}
		}

		return Optional.empty();
	}

	protected <X> Optional<X> convertToOptionalInstance(Class<X> type, List<V4Message> out) {
		for (V4Message msg : out) {
			X o = getRelevantObject(msg, type);
			if (o != null) {
				return Optional.of((X) o);
			}
		}

		return Optional.empty();
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, String t, SymphonyAddressable address) {
		return getRelevantObject(getLastEntityJsonFromHistory(type, t, address), type);
	}

	@Override
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, String t, SymphonyAddressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, null, t);
		PaginationAttribute pa = new PaginationAttribute(0, 1);
		List<V4Message> out = messageApi.searchMessages(msq, pa);
		return convertToOptionalEntityJson(out);
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, String t, SymphonyAddressable address, Instant since) {
		return getFromEntityJson(getEntityJsonFromHistory(t, address, since), type);
	}

	@Override
	public <X> List<X> getFromEntityJson(List<EntityJson> ej, Class<X> type) {
		return ej.stream().map(i -> getRelevantObject(Optional.of(i), type)).filter(o -> o.isPresent())
				.map(o -> o.get()).collect(Collectors.toList());
	}

	@Override
	public List<EntityJson> getEntityJsonFromHistory(String t, SymphonyAddressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, since, t);
		PaginationAttribute pa = new PaginationAttribute(0, 50);
		List<V4Message> out = messageApi.searchMessages(msq, pa);
		return out.stream().map(msg -> getEntityJson(msg)).filter(e -> e != null).collect(Collectors.toList());
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
	public <X> List<X> getFromHistory(Class<X> type, SymphonyAddressable address, Instant since) {
		return getEntityJsonFromHistory(type, address, since).stream()
				.map(ej -> getRelevantObject(Optional.of(ej), type)).filter(o -> o.isPresent()).map(o -> o.get())
				.collect(Collectors.toList());
	}

	@Override
	public <X> List<EntityJson> getEntityJsonFromHistory(Class<X> type, SymphonyAddressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, since, null);
		PaginationAttribute pa = new PaginationAttribute(0, 50);
		List<V4Message> out = messageApi.searchMessages(msq, pa);
		return out.stream().map(msg -> getEntityJson(msg)).filter(e -> e != null).collect(Collectors.toList());
	}

	private <X> MessageSearchQuery createMessageSearchQuery(Class<X> type, Addressable address, Instant since, String t) {
		MessageSearchQuery msq = new MessageSearchQuery();
		if (address instanceof SymphonyAddressable) {
			msq.setStreamId(sr.getStreamFor((SymphonyAddressable) address));
		}

		if (since != null) {
			msq.fromDate(since.toEpochMilli());
		}

		if (type != null) {
			msq.setHashtag(SymphonyTagSupport.formatTag(type));
		} else if (t != null) {
			msq.setHashtag(t);
		}

		return msq;
	}

	@Override
	public boolean isSupported(Addressable a) {
		return a instanceof SymphonyAddressable;
	}
}
