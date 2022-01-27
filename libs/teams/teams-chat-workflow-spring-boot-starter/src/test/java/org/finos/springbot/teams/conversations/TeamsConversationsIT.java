package org.finos.springbot.teams.conversations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = { 
		TeamsConversationsConfig.class, 
	})
@TestPropertySource(properties = {
	"teams.bot.MicrosoftAppId=${microsoft-app-id}",
	"teams.bot.MicrosoftAppPassword=${microsoft-app-password}",
	"teams.app.tennantId=${microsoft-tennant-id}"
})
public class TeamsConversationsIT {

	
	@Autowired
	TeamsConversations tc;
	
	@Test
	public void simpleTest() throws Exception {
		tc.getAllAddressables();
	}
	
}

