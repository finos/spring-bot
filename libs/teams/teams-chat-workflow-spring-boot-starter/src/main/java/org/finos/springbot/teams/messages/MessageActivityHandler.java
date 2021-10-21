package org.finos.springbot.teams.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.teams.TeamsChannelData;

public class MessageActivityHandler extends ActivityHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	TeamsHTMLParser messageParser;
	//EntityJsonConverter jsonConverter;
	List<ActionConsumer> messageConsumers;
	TeamsConversations teamsConversations;
	
	public MessageActivityHandler(List<ActionConsumer> messageConsumers, TeamsConversations teamsConversations, TeamsHTMLParser parser) {
		super();
		this.messageConsumers = messageConsumers;
		this.teamsConversations = teamsConversations;
		this.messageParser = parser;
	}

	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
		Activity a = turnContext.getActivity();
		TeamsChannelData tcd = a.teamsGetChannelData();
		Object data = a.getChannelData();
	
		Message message = createMessageFromActivity(a);
		Addressable rr = teamsConversations.getTeamsChat(tcd);
		User u = teamsConversations.getUser(a.getFrom());
		
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
		
		// errors are handled using Spring's ErrorHandler rather than this.
		return new CompletableFuture<Void>();
	}

	private Message createMessageFromActivity(Activity a) {
		if (a.getAttachments() != null) {
			List<Attachment> attachments = new ArrayList<>(a.getAttachments());
			Collections.reverse(attachments);
			for (Attachment attachment : attachments) {
				if (MediaType.TEXT_HTML_VALUE.equals(attachment.getContentType())) {
					return messageParser.apply((String) attachment.getContent(), a.getEntities());
				}
			}
		}
		
		return Message.of(a.getText());
	}
}
