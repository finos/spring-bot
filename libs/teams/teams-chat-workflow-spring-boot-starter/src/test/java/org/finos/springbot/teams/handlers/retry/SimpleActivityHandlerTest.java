package org.finos.springbot.teams.handlers.retry;

import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.handlers.SimpleActivityHandler;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.microsoft.bot.schema.Activity;

@SpringBootTest(classes = { 
		DataHandlerConfig.class, 
	})
public class SimpleActivityHandlerTest {
	
	@Mock
	TeamsConversations tc;
	
	@InjectMocks
	private SimpleActivityHandler handler = new SimpleActivityHandler(tc);
	
	@Test
	public void testHandleActivity() {
		Activity activity = Mockito.mock(Activity.class);
		handler.handleActivity(activity , new TeamsChannel());
	}
}
