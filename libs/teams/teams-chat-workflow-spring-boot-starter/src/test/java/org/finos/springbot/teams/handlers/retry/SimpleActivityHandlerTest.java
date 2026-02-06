package org.finos.springbot.teams.handlers.retry;

import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.handlers.SimpleActivityHandler;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.microsoft.bot.schema.Activity;

import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {
		DataHandlerConfig.class,
})
@ExtendWith(MockitoExtension.class)
public class SimpleActivityHandlerTest {

	@Mock
	TeamsConversations tc;

	@InjectMocks
	private SimpleActivityHandler handler = new SimpleActivityHandler(tc);

	@Test
	public void testHandleActivity() {
		Activity activity = mock(Activity.class);
		handler.handleActivity(activity , new TeamsChannel());
	}
}