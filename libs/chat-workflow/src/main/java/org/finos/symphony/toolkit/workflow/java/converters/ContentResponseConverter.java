package org.finos.symphony.toolkit.workflow.java.converters;

import java.util.Arrays;

import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;

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
			Message m = t instanceof Message ? (Message) t : Message.of(Arrays.asList((Content) t));
			MessageResponse out = new MessageResponse(u.action().getAddressable(), m);
			rh.accept(out);
		}
	}

}
