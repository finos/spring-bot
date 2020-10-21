package com.github.deutschebank.symphony.workflow;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.history.MessageHistory;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.symphony.api.model.MessageSearchQuery;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageList;

public class TestHistory extends AbstractMockSymphonyTest {
	
	
	MessageHistory mh;
	
	@Autowired
	Workflow wf;
	
	@MockBean
	SymphonyRooms ru;
	
	EntityJsonConverter ejc;
	
	@Before
	public void setup() {
		ejc = new EntityJsonConverter(wf);
		mh = new MessageHistory(wf, ejc, messagesApi, ru);
	}
	

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
		
		TestObjects out = mh.getLastFromHistory(TestObjects.class, new RoomDef("someroom", "", true, "abc123"))
			.orElseThrow(() -> new RuntimeException());
		
		Assert.assertEquals(out, to);
		Assert.assertFalse(out == to);
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
				new RoomDef("someroom", "", true, "abc123"),
				Instant.now().minus(10, ChronoUnit.DAYS));
		
		Assert.assertEquals(3, out.size());
		Assert.assertEquals(one, out.get(0));
		Assert.assertEquals(two, out.get(1));
		Assert.assertEquals(three, out.get(2));
		
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
		return new V4Message()
			.message("some stuff")
			.data(ejc.toWorkflowJson(one));
	}
}
