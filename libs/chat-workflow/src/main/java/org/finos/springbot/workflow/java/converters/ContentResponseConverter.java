package org.finos.springbot.workflow.java.converters;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;

public class ContentResponseConverter extends AbstractResponseConverter {

	public ContentResponseConverter(ResponseHandlers rh) {
		super(rh);
	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	@Override
	public void accept(Object t, ChatHandlerExecutor u) {
		if (t instanceof Content) {
			Message m = t instanceof Message ? (Message) t : Message.of((Content) t);
			MessageResponse out = new MessageResponse(u.action().getAddressable(), m);
			rh.accept(out);
		}
	}

}
