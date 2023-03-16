package org.finos.springbot.teams.handlers.retry;

import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { 
		DataHandlerConfig.class, 
	})
public class NoOpRetryHandlerTest {
	
	public NoOpRetryHandler noOpRetryHandler = new NoOpRetryHandler();
	
	@Test
	public void getTest() {
		Assertions.assertFalse(noOpRetryHandler.get().isPresent());
	}
	
	@Test
	public void handleExceptionTest() {
		Response t = new MessageResponse(new TeamsChannel(),"");
		int retryCount=1;
		Assertions.assertFalse(noOpRetryHandler.handleException(t,retryCount, new Exception()));
	}
}
