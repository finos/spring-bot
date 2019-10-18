package com.symphony;

import java.io.File;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageList;

@Ignore
public class CXFMultipartTest {
	

	@Test
	public void testAttachmentPosting() throws Exception  {
		TestClientStrategy strategy = TestPodConfig.CXF_CERT;
		MessagesApi messagesApi = strategy.getAgentApi(MessagesApi.class);
		
		// pull some messages back
		V4MessageList msg = messagesApi.v4StreamSidMessageGet(AbstractTest.ROOM, 0l,null,  null, 0, 100);
		Assert.assertTrue(msg.size() > 4);
			
		// post a message
		String message = "<messageML>Hello Java Java World!</messageML>";
		
		File f = new File(this.getClass().getResource("/walker.jpeg").getFile());
		Attachment a = new Attachment("attachment", "image/jpeg", f);
		
		V4Message response = messagesApi.v4StreamSidMessageCreatePost(null, AbstractTest.ROOM, message, null, null, f , null, null);
		messagesApi.v4StreamSidMessageCreatePost(null, AbstractTest.ROOM, message, null, null, a , null, null);
		System.out.println(response.toString());
	}
}
