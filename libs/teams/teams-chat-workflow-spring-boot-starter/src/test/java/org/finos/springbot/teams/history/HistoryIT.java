package org.finos.springbot.teams.history;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = { 
	MockTeamsConfiguration.class, 
	TeamsWorkflowConfig.class,
})
@ActiveProfiles(value = "teams")
public class HistoryIT {
	
	@Autowired
	TeamsHistory mh;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Test
	public void testPutAndGetLast() {
		Ent
		mh.store(getTeamsChat(), );
		
		TestObjects to = new TestObjects();
		Mockito.when(messagesApi.v1MessageSearchPost(Mockito.any(), 
			Mockito.isNull(), Mockito.isNull(), Mockito.anyInt(), 
			Mockito.anyInt(), Mockito.any(), Mockito.any())).thenAnswer(a -> {
				V4MessageList out = new V4MessageList();
				out.addAll(Arrays.asList(makeMessage(to)));
					
				return out;
			});
		
		TestObjects out = mh.getLastFromHistory(TestObjects.class, getTeamsChat())
			.orElseThrow(() -> new RuntimeException());
		
		Assertions.assertEquals(out, to);
		Assertions.assertFalse(out == to);
	}

	private TeamsChat getTeamsChat() {
		return new TeamsChat("test-data-example", "Test Example");
	}
	
	@Test
	public void testFindInHistory() {
		
		TestObjects one = new TestObjects();
		TestObjects two = new TestObjects();
		TestObjects three = new TestObjects();
		
		EntityJson ej = new EntityJson();
		ej.put("one", one);
		ej.put("two", one);
		ej.put("three", one);

		Mockito.when(messagesApi.v1MessageSearchPost(Mockito.any(), 
			Mockito.isNull(), Mockito.isNull(), Mockito.anyInt(), 
			Mockito.anyInt(), Mockito.any(), Mockito.any())).thenAnswer(a -> {
				V4MessageList out = new V4MessageList();
				out.addAll(Arrays.asList(makeMessage(one), makeMessage(two), makeMessage(three)));
					
				return out;
			});
		
		
		List<TestObjects> out = mh.getFromHistory(
				TestObjects.class, 
				getTeamsChat(),
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
}
