package com.github.deutschebank.symphony.workflow.sources.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.deutschebank.symphony.workflow.AbstractNeedsWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.sources.symphony.TagSupport;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;
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
		wf.registerHistoryProvider(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
		String hashtag = TagSupport.formatTag(type);
		MessageSearchQuery msq = new MessageSearchQuery().hashtag(hashtag).streamId(ru.getStreamFor(address));
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 1, null, null);
		
		if (out.size() == 1) {
			Object o = jsonConverter.readWorkflowValue(out.get(0).getData());
			if (type.isAssignableFrom(o.getClass())) {
				return Optional.of((X) o);
			} else {
				throw new RuntimeException("Tagged with "+hashtag+" but actually a" +o.getClass());
			}
		} else {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
		String hashtag = TagSupport.formatTag(type);
		MessageSearchQuery msq = new MessageSearchQuery()
				.hashtag(hashtag)
				.fromDate(since.toEpochMilli())
				.streamId(ru.getStreamFor(address));
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 50, null, null);
				
		return out.stream()
			.map(msg -> {
				try {
					Object done = jsonConverter.readWorkflowValue(msg.getData());
					if (type.isAssignableFrom(done.getClass())) {
						return (X) done;
					} else {
						return null;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			})
			.filter(e -> e != null)
			.collect(Collectors.toList());

	}
}
