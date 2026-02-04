package org.finos.springbot.teams.handlers.retry;

import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.handlers.SimpleActivityHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microsoft.bot.schema.Activity;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimpleActivityHandlerTest {
	
	@Mock
	TeamsConversations tc;
	
	@InjectMocks
	private SimpleActivityHandler handler;

	@Test
	public void testHandleActivity() {
		Activity activity = mock(Activity.class);
		handler.handleActivity(activity , new TeamsChannel());
	}
}
