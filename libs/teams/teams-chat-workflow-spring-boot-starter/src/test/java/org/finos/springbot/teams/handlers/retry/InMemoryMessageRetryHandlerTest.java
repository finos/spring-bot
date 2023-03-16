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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { 
		DataHandlerConfig.class, 
	})

public class InMemoryMessageRetryHandlerTest {
	
	InMemoryMessageRetryHandler inMemoryMessageRetryHandler = new InMemoryMessageRetryHandler();
	TeamsChannel dummyChat = new TeamsChannel();
	Object dummyObject = new Object();
	
	@BeforeEach
	public void setUp() {
		while(inMemoryMessageRetryHandler.queue.poll()!=null);
		LocalDateTime currentTime= LocalDateTime.now();
		Map<String, Object> data1 = new HashMap<>();
		data1.put("key1", dummyObject);
		Map<String, Object> data2 = new HashMap<>();
		data2.put("key2", dummyObject);
		Response r1 = new DataResponse(dummyChat, data1, "");
		Response r2 = new DataResponse(dummyChat, data2, "");
		MessageRetry t1 = new MessageRetry(r1,3,5,currentTime.plusSeconds(5));
		MessageRetry t2 = new MessageRetry(r2,3,45,currentTime.plusSeconds(45));
		inMemoryMessageRetryHandler.queue.add(t1);
		inMemoryMessageRetryHandler.queue.add(t2);
	}
	@Test
	public void testGet() throws InterruptedException {
		Map<String, Object> expectedData = new HashMap<>();
		expectedData.put("key1", dummyObject);
		LocalDateTime futureTime = LocalDateTime.now().plusSeconds(10);
		while(LocalDateTime.now().isBefore(futureTime)) {
			Thread.sleep(1000);
		}
		DataResponse actualResponse = (DataResponse) inMemoryMessageRetryHandler.get().get().getResponse();
		Assertions.assertTrue(actualResponse.getData().equals(expectedData));
		Optional<MessageRetry> actualResult = inMemoryMessageRetryHandler.get();
		Assertions.assertFalse(actualResult.isPresent());
		
	}
	
	@Test 
	public void testGetNoData() throws InterruptedException {
		LocalDateTime futureTime = LocalDateTime.now().plusSeconds(1);
		while(LocalDateTime.now().isBefore(futureTime)) {
			Thread.sleep(1000);
		}
		Optional<MessageRetry> actualResponse = inMemoryMessageRetryHandler.get();
		Assertions.assertFalse(actualResponse.isPresent());
	}
}
