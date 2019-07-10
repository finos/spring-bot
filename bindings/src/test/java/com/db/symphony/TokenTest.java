package com.db.symphony;

import org.junit.Assert;
import org.junit.experimental.theories.Theory;

import com.symphony.api.TokenManager;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4MessageList;

public class TokenTest extends AbstractTest {
	
	@Theory
	public void checkTokenCreation(TestClientStrategy s) throws Exception  {
		TokenManager tm = s.getTokenManager();
		
		MessagesApi messagesApi = s.getAgentApi(MessagesApi.class);
		
		// pull some messages back: look - no tokens are set
		V4MessageList msg = messagesApi.v4StreamSidMessageGet(ROOM, 0l, null,  null, 0, 5);
		Assert.assertTrue(msg.size() > 4);
		
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
		Assert.assertNotEquals(originalSessionToken, tm.getSessionToken());
		Assert.assertNotEquals(originalKeyManagerToken, tm.getKeyManagerToken());
		
	}
	
}
