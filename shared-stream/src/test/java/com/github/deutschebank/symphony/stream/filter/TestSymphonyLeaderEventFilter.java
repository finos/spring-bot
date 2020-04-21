package com.github.deutschebank.symphony.stream.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.github.deutschebank.symphony.stream.log.LogMessage;
import com.github.deutschebank.symphony.stream.log.LogMessageHandlerImpl;
import com.github.deutschebank.symphony.stream.log.LogMessageType;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;

public class TestSymphonyLeaderEventFilter {
	
	static class SimpleEventConsumer implements StreamEventConsumer {
		
		private Participant p;

		public SimpleEventConsumer(Participant p) {
			super();
			this.p = p;
		}

		public List<V4Event> collection = new ArrayList<V4Event>();
		
		@Override
		public void accept(V4Event t) {
			collection.add(t);
		}
	}
	
	LogMessageHandlerImpl lmh = new LogMessageHandlerImpl("abc123", null, "UNIT");
	
	@Test
	public void testMultipleStreamingConsumers() {
		List<SimpleEventConsumer> consumers = IntStream.range(0, 4)
			.mapToObj(i -> new Participant("p"+i))
			.map(p -> new SimpleEventConsumer(p))
			.collect(Collectors.toList());
		
		/*MessagesApi messagesAPI = Mockito.mock(MessagesApi.class);
		
		Mockito.when(messagesAPI.v4StreamSidMessageCreatePost(
			Mockito.isNull(), 
			Mockito.anyString(), 
			Mockito.anyString(),
			Mockito.anyString(), 
			Mockito.isNull(), 
			Mockito.isNull(),
			Mockito.isNull(),
			Mockito.isNull())).then(a -> {
				
				System.out.println("Posting");
				
			});
		
		
		ListBackedSharedLog lbsl = new ListBackedSharedLog(null);*/
		
		List<Participant> participantMessages = new ArrayList<Participant>(); 
		
		
		List<SymphonyLeaderEventFilter> wrapped = consumers.stream()
			.map(c -> new SymphonyLeaderEventFilter(c, false, c.p, lmh, 
				m -> participantMessages.add(m.getParticipant())))
			.collect(Collectors.toList());


		
		// send some messsages through
		for (int j = 0; j < 8; j++) {
			V4Event lm = createLeaderEvent(j % 4);
			pipeEvent(lm, wrapped);

			for (int k = 0; k < 25; k++) {
				V4Event me = createMessageEvent(k);
				pipeEvent(me, wrapped);
			}
		}
		
		// check that each consumer has the right number of messages
		consumers.forEach(c -> {
			Assert.assertEquals(50, c.collection.size());
		});
		
		// make sure we logged the participants correctly too
		// this will receive one message from each leader change, by 4 listeners.
		Assert.assertEquals(4 * 8, participantMessages.size());
	}



	private V4Event createMessageEvent(int k) {
		return new V4Event()
				.payload(new V4Payload()
					.messageSent(new V4MessageSent()
						.message(new V4Message()
							.message("some message"))));
	}

	private void pipeEvent(V4Event lm, List<SymphonyLeaderEventFilter> wrapped) {
		wrapped.forEach(w -> w.accept(lm));
	}

	private V4Event createLeaderEvent(int i) {
		LogMessage lm = new LogMessage(new Participant("p"+i), LogMessageType.LEADER);
		return new V4Event()
				.payload(new V4Payload()
					.messageSent(new V4MessageSent()
						.message(new V4Message()
							.data(lmh.serializeJson(lm))
							.message(lmh.createMessageML(lm)))));
	}
}
