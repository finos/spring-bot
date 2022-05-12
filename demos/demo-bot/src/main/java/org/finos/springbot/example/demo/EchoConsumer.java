package org.finos.springbot.example.demo;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This is a demonstration of how you can intercept actions to the bots, and perform your own processing
 * of them over and above that provided by Spring Bot's built-in {@link ActionConsumer}s.
 * 
 * @author rob@kite9.com
 *
 */
@Component
public class EchoConsumer implements ActionConsumer {

	@Autowired
	ResponseHandlers rh;

	@Override
	public void accept(Action event) {
		if (event instanceof SimpleMessageAction) {
			// reply with original content
			SimpleMessageAction in = (SimpleMessageAction) event;
			// reply to the room the message came from
			Addressable from = in.getAddressable();
			Message msg = in.getMessage();
			// Ignore commands
			if (!msg.getText().startsWith("/")) {
				MessageResponse mr = new MessageResponse(from, msg);
				rh.accept(mr);
			}

		}
	}

}
