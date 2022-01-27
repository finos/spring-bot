package org.finos.springbot.workflow.welcome;

import java.util.function.Function;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.MemberAction;
import org.finos.springbot.workflow.actions.MemberAction.Type;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;

/**
 * This class allows you to configure room welcome messages.  This is not configured by default 
 * and needs to be enabled.
 * 
 * @author rob@kite9.com
 *
 */
public class RoomWelcomeEventConsumer implements ActionConsumer {
	
	private ResponseHandlers rh;
	private final Function<MemberAction, Message> welcomeMessageBuilder;
	
	public final static Function<MemberAction, Message> DEFAULT_MESSAGE_BUILDER = ma -> { 
		return Message.of("Welcome "+ma.getUser().getName()+" to "+ma.getAddressable().getName()+".  Please type /help if you want to talk to me.");
	};
	
	public RoomWelcomeEventConsumer(ResponseHandlers rh, Function<MemberAction, Message> welcomeMessageBuilder) {
		super();
		this.rh = rh;
		this.welcomeMessageBuilder = welcomeMessageBuilder;
	}
	
	public RoomWelcomeEventConsumer(ResponseHandlers rh) {
		this(rh, DEFAULT_MESSAGE_BUILDER);
	}

	@Override
	public void accept(Action t) {
		if (t instanceof MemberAction) {
			MemberAction ma = (MemberAction) t;
			if (ma.getType() == Type.ADDED) {
				Message out = welcomeMessageBuilder.apply(ma);
				MessageResponse mr = new MessageResponse(ma.getAddressable(), out);
				rh.accept(mr);
			}
		}
	}
	
	
}
