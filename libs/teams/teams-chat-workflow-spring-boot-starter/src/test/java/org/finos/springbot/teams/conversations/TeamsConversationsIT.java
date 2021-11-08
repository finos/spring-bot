package org.finos.springbot.teams.conversations;

import org.finos.springbot.teams.content.TeamsChat;
import org.junit.jupiter.api.Test;

public class TeamsConversationsIT {

	String tenantId = "2f758e82-b31e-4a99-a9dd-e4d4abe351db";
	
	@Test
	public void simpleTest() throws Exception {
		TeamsConversationsImpl tc = new TeamsConversationsImpl("abc13234");
		tc.afterPropertiesSet();
		
		tc.getChatAdmins(new TeamsChat("19:lpBLKAwWu2xklnZ8jmzmhTP_bjSbTYo5xULFVdGi2481@thread.tacv2", "HEYNOW"));
	}
}
