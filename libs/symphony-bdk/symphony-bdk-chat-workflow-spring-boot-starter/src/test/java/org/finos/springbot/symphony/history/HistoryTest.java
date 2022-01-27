package org.finos.springbot.symphony.history;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.SymphonyMockConfiguration;
import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.MessageSearchQuery;
import com.symphony.bdk.gen.api.model.V4Message;

@SpringBootTest(classes = { 
	SymphonyMockConfiguration.class, 
	SymphonyWorkflowConfig.class,
})
@ActiveProfiles(value = "symphony")
public class HistoryTest {
	
	@Autowired
	SymphonyHistory mh;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@MockBean
	MessageService messagesApi;

	@Test
	public void testGetLast() {
		TestObjects to = new TestObjects();
		Mockito.when(messagesApi.searchMessages(Mockito.any(), Mockito.any())) 
			.thenAnswer(a -> Arrays.asList(makeMessage(to)));
		
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

		Mockito.when(messagesApi.searchMessages(Mockito.any(), Mockito.any())) 
			.thenAnswer(a -> Arrays.asList(makeMessage(one), makeMessage(two), makeMessage(three)));
		
		
		List<TestObjects> out = mh.getFromHistory(
				TestObjects.class, 
				new SymphonyRoom("someroom", "abc123"),
				Instant.now().minus(10, ChronoUnit.DAYS));
		
		Assertions.assertEquals(3, out.size());
		Assertions.assertEquals(one, out.get(0));
		Assertions.assertEquals(two, out.get(1));
		Assertions.assertEquals(three, out.get(2));
		
		Mockito.verify(messagesApi).searchMessages(
			Mockito.argThat(e -> {
				MessageSearchQuery msq = (MessageSearchQuery) e;
				return msq.getHashtag().equals(TestObjects.class.getCanonicalName().replace(".", "-").toLowerCase());
			}),
			Mockito.isA(PaginationAttribute.class));
		
	}


	protected V4Message makeMessage(TestObjects one) {
		EntityJson out = new EntityJson();
		out.put("1", one);
		return new V4Message()
			.message("some stuff")
			.data(ejc.writeValue(out));
	}
}
