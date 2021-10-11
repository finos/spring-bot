package org.finos.springbot.sources.teams.messages;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.sources.teams.conversations.TeamsConversations;
import org.finos.springbot.sources.teams.turns.CurrentTurnContext;
import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.actions.consumers.ActionConsumer;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.teams.TeamsChannelData;

public class MessageActivityHandler extends ActivityHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	//MessageMLParser messageParser;
	//EntityJsonConverter jsonConverter;
	List<ActionConsumer> messageConsumers;
	TeamsConversations teamsConversations;
	
	public MessageActivityHandler(List<ActionConsumer> messageConsumers, TeamsConversations teamsConversations) {
		super();
		this.messageConsumers = messageConsumers;
		this.teamsConversations = teamsConversations;
	}

	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
		Activity a = turnContext.getActivity();
		String text = a.getText();
		TeamsChannelData tcd = a.teamsGetChannelData();
		Object data = a.getChannelData();
	
		Message message = Message.of(text);
		Addressable rr = teamsConversations.getTeamsChat(tcd);
		User u = null; //teamsConversations.loadUserById(a.get);
		
		rr = rr == null ? u : rr;
		SimpleMessageAction sma = new SimpleMessageAction(rr, u, message, data);
		
		CurrentTurnContext.CURRENT_CONTEXT.set(turnContext);
		
		try {
			Action.CURRENT_ACTION.set(sma);
			for (ActionConsumer c : messageConsumers) {
				c.accept(sma);
			}
		} finally {
			Action.CURRENT_ACTION.set(Action.NULL_ACTION);
		}
			
		CurrentTurnContext.CURRENT_CONTEXT.set(null);
	}
}
