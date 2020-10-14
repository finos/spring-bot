package com.github.deutschebank.symphony.workflow.sources.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.deutschebank.symphony.workflow.AbstractNeedsWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Tag;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.sources.symphony.TagSupport;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
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

	@SuppressWarnings("unchecked")
	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, null, null);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 1, null, null);
		
		for (V4Message msg : out) {
			Object o = jsonConverter.readWorkflowValue(msg.getData());
			if ((o != null) && (type.isAssignableFrom(o.getClass()))) {
				return Optional.of((X) o);
			}
		}

		return Optional.empty();
	}
	

	@Override
	public List<Object> getFromHistory(Tag t, Addressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(null, address, since, t);
		V4MessageList out = messageApi.v1MessageSearchPost(msq, null, null, 0, 50, null, null);
		
		return out.stream()
				.map(msg -> {
					try {
						return jsonConverter.readWorkflowValue(msg.getData());
					} catch (Exception e1) {
						e1.printStackTrace();
						return null;
					}
				})
				.filter(e -> e != null)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
		MessageSearchQuery msq = createMessageSearchQuery(type, address, since, null);
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
