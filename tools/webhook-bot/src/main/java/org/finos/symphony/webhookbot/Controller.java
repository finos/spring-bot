package org.finos.symphony.webhookbot;

import org.springframework.web.bind.annotation.PostMapping;

public class Controller {

	
	@PostMapping(path = "hook/{streamId}/{hookId}")
	public void receiveWebhook() {
		
		
	}
}
