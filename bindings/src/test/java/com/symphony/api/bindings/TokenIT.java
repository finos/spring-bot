package com.symphony.api.bindings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4MessageList;

public class TokenIT extends AbstractIT {
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void checkTokenCreation(TestClientStrategy s) throws Exception  {
		TokenManager tm = s.getTokenManager();
		
		MessagesApi messagesApi = s.getAgentApi(MessagesApi.class);
		
		// pull some messages back: look - no tokens are set
		V4MessageList msg = messagesApi.v4StreamSidMessageGet(ROOM, 0l, null,  null, 0, 5);
		Assertions.assertTrue(msg.size() > 4);
		
		String originalSessionToken = tm.getSessionToken().getToken();
		String originalKeyManagerToken = tm.getKeyManagerToken().getToken();
		
		// expire the session token
		try {
			s.getSessionAuthApi().v1LogoutPost(tm.getSessionToken().getToken());
		} catch (Exception e) {
			// doesn't return what symphony says it will
		}

		// this will perform a retry, since the original tokens are invalid.
		messagesApi.v4StreamSidMessageGet(ROOM, 0l, null,  null, 0, 100);
		
		// check that tokens actually did get refreshed
		Assertions.assertNotEquals(originalSessionToken, tm.getSessionToken());
		Assertions.assertNotEquals(originalKeyManagerToken, tm.getKeyManagerToken());
		
	}
	
}
