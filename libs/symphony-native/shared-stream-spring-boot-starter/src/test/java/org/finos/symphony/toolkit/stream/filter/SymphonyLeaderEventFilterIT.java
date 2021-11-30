package org.finos.symphony.toolkit.stream.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.TestApplication;
import org.finos.symphony.toolkit.stream.handler.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.handler.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.log.LogMessageType;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Event;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("develop")
public class SymphonyLeaderEventFilterIT {
	
	// now using a room just for this purpose
	private String streamId = "QTG/xBPcpYtbMbfhGrjK7X///okjuFBXdA==";
	private String clusterName = "test";
	
	@Autowired
	MessagesApi messagesApi;
		
	@Autowired
	ApiInstance singleApi;
	
	@Autowired
	ExceptionConsumer ec;
	
	SymphonyRoomSharedLog lmh;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Autowired
	ApplicationContext ac;

	
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
	
	@Test
	public void testMultipleStreamingConsumers() throws InterruptedException {
		
		List<SimpleEventConsumer> consumers = IntStream.range(0, 4)
			.mapToObj(i -> new Participant("p"+i))
			.map(p -> new SimpleEventConsumer(p))
			.collect(Collectors.toList());
		
		List<SymphonyStreamHandler> wrapped = consumers.stream()
			.map(c -> {
				lmh = new SymphonyRoomSharedLog(clusterName, streamId, messagesApi, "UNIT", 6000, ejc);
				SymphonyStreamHandler out = new SymphonyStreamHandler(singleApi, c, ec, false);
				out.setFilter(new SymphonyLeaderEventFilter(c.p, lmh));
				out.start();
				return out;
			})
			.collect(Collectors.toList());


		
		// send some messsages through
		for (int j = 0; j < 8; j++) {
			createLeaderEvent(j % 4);

			for (int k = 0; k < 3; k++) {
				createMessageEvent(k);
			}
		}
		
		boolean done = false;
		while (!done) {
			Thread.sleep(2000);
			done = consumers.stream()
				.map(c -> c.collection.size() == 6)
				.reduce(true, (a, b) -> a && b);
		
		}
		
		wrapped.stream().forEach(c -> c.stop());
	}



	private void createMessageEvent(int k) {
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>some message</messageML>", null, null, null, null, null);
	}


	private void createLeaderEvent(int i) {
		LogMessage lm = new LogMessage("test", new Participant("p"+i), LogMessageType.LEADER);
		String messageMl = lmh.createMessageML(lm);
		String json = lmh.serializeJson(lm);
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, messageMl, json, null, null, null, null);
	}
}
