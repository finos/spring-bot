package com.symphony;

import java.io.File;

import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Message;


public class JerseyMultipartTest {


	@Test
	public void testMultipartWithTyping() throws Exception {
		MessagesApi api = TestPodConfig.JERSEY_RSA.getAgentApi(MessagesApi.class);
	
		String message = "<messageML>Hello Jersey Typed World!</messageML>";

		File f = new File(this.getClass().getResource("/walker.jpeg").getFile());
		FileDataBodyPart attachment = new FileDataBodyPart("attachment", f);
		
		V4Message out = api.v4StreamSidMessageCreatePost(null, AbstractTest.ROOM, message, null, null, attachment, null, null);
		System.out.println(out);
	} 
	
}
