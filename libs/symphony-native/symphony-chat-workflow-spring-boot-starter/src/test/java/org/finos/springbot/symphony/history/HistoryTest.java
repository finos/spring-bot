package org.finos.springbot.symphony.history;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.finos.springbot.symphony.SymphonyMockConfiguration;
import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.json.EntityJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.MessageSearchQuery;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageList;

@SpringBootTest(classes = { 
		SymphonyMockConfiguration.class, 
	SymphonyWorkflowConfig.class,
})
public class HistoryTest {
	
	@Autowired
	SymphonyHistory mh;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Autowired
	MessagesApi messagesApi;

	@Test
	public void testGetLast() {
		TestObjects to = new TestObjects();
		Mockito.when(messagesApi.v1MessageSearchPost(Mockito.any(), 
			Mockito.isNull(), Mockito.isNull(), Mockito.anyInt(), 
			Mockito.anyInt(), Mockito.any(), Mockito.any())).thenAnswer(a -> {
				V4MessageList out = new V4MessageList();
				out.addAll(Arrays.asList(makeMessage(to)));
					
				return out;
			});
		
		TestObjects out = mh.getLastFromHistory(TestObjects.class, new SymphonyRoom("someroom", "abc123"))
			.orElseThrow(() -> new RuntimeException());
		
		Assertions.assertEquals(out, to);
		Assertions.assertFalse(out == to);
	}
	
	@Test
	public void testFindInHistory() {
		
		TestObjects one = new TestObjects();
		TestObjects two = new TestObjects();
		TestObjects three = new TestObjects();

		Mockito.when(messagesApi.v1MessageSearchPost(Mockito.any(), 
			Mockito.isNull(), Mockito.isNull(), Mockito.anyInt(), 
			Mockito.anyInt(), Mockito.any(), Mockito.any())).thenAnswer(a -> {
				V4MessageList out = new V4MessageList();
				out.addAll(Arrays.asList(makeMessage(one), makeMessage(two), makeMessage(three)));
					
				return out;
			});
		
		
		List<TestObjects> out = mh.getFromHistory(
				TestObjects.class, 
				new SymphonyRoom("someroom", "abc123"),
				Instant.now().minus(10, ChronoUnit.DAYS));
		
		Assertions.assertEquals(3, out.size());
		Assertions.assertEquals(one, out.get(0));
		Assertions.assertEquals(two, out.get(1));
		Assertions.assertEquals(three, out.get(2));
		
		Mockito.verify(messagesApi).v1MessageSearchPost(
			Mockito.argThat(e -> {
				MessageSearchQuery msq = (MessageSearchQuery) e;
				return msq.getHashtag().equals(TestObjects.class.getCanonicalName().replace(".", "-").toLowerCase());
			}),
			Mockito.isNull(),
			Mockito.isNull(),
			Mockito.eq(0), 
			Mockito.eq(50), 
			Mockito.isNull(),
			Mockito.isNull());
		
	}


	protected V4Message makeMessage(TestObjects one) {
		EntityJson out = new EntityJson();
		out.put("1", one);
		return new V4Message()
			.message("some stuff")
			.data(ejc.writeValue(out));
	}
}
