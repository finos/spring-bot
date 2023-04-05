package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;
import java.util.Optional;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.handlers.retry.AbstractRetryingActivityHandler.MessageRetry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InMemoryRetryingActivityHandlerTest {

	private InMemoryRetryingActivityHandler inhandler = new InMemoryRetryingActivityHandler(null);
	TeamsChannel dummyChat1 = new TeamsChannel("dummy_id_1", "dummy_name");
	TeamsChannel dummyChat2 = new TeamsChannel("dummy_id_2", "dummy_name");
	
	private void setUp(LocalDateTime retryTime) {
		MessageRetry mr = inhandler.new MessageRetry(null,dummyChat1,3, retryTime);
		inhandler.add(mr);
	}
	
	@Test
	public void testFoundMessageForRetry() {
		setUp(LocalDateTime.now().minusSeconds(100));
		setUp(LocalDateTime.now().plusSeconds(100));
		
		TeamsAddressable address = inhandler.get().get().getAddressable();
		Assertions.assertTrue(address.getKey().equals(dummyChat1.getKey()));
	}
	
	@Test 
	public void testNoMessageEligibleForRetry(){
		setUp(LocalDateTime.now().plusSeconds(100));
		setUp(LocalDateTime.now().plusSeconds(150));
		
		Optional<MessageRetry> actualResponse = inhandler.get();
		Assertions.assertFalse(actualResponse.isPresent());
	}
	
}
