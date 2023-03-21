package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { 
		DataHandlerConfig.class, 
	})

public class InMemoryMessageRetryHandlerTest {
	
	InMemoryMessageRetryHandler inMemoryMessageRetryHandler = new InMemoryMessageRetryHandler();
	TeamsChannel dummyChat = new TeamsChannel();
	Object dummyObject = new Object();
	
	public void setUp(LocalDateTime retryTime1,LocalDateTime retryTime2) {
		inMemoryMessageRetryHandler.clearAll();
		Map<String, Object> data1 = new HashMap<>();
		data1.put("key1", dummyObject);
		Map<String, Object> data2 = new HashMap<>();
		data2.put("key2", dummyObject);
		Response r1 = new DataResponse(dummyChat, data1, "");
		Response r2 = new DataResponse(dummyChat, data2, "");
		MessageRetry t1 = new MessageRetry(r1,3,45,retryTime1);
		MessageRetry t2 = new MessageRetry(r2,3,45,retryTime2);
		inMemoryMessageRetryHandler.add(t1);
		inMemoryMessageRetryHandler.add(t2);
	}
	@Test
	public void testFoundMessageForRetry() {
		setUp(LocalDateTime.now().minusSeconds(100),LocalDateTime.now().plusSeconds(150));
		Map<String, Object> expectedData = new HashMap<>();
		expectedData.put("key1", dummyObject);
		DataResponse actualResponse = (DataResponse) inMemoryMessageRetryHandler.get().get().getResponse();
		Assertions.assertTrue(actualResponse.getData().equals(expectedData));
	}
	
	@Test 
	public void testNoMessageEligibleForRetry(){
		setUp(LocalDateTime.now().plusSeconds(100),LocalDateTime.now().plusSeconds(150));
		Optional<MessageRetry> actualResponse = inMemoryMessageRetryHandler.get();
		Assertions.assertFalse(actualResponse.isPresent());
	}
	
	@Test 
	public void testNoMessageInQueue() {
		inMemoryMessageRetryHandler.clearAll();
		Optional<MessageRetry> actualResponse = inMemoryMessageRetryHandler.get();
		Assertions.assertFalse(actualResponse.isPresent());
		
	}
}
