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
import org.finos.springbot.workflow.form.FormConverter;
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
	List<ActionConsumer> messageConsumers;
	TeamsConversations teamsConversations;
	FormConverter formConverter;
	
	public MessageActivityHandler(
			List<ActionConsumer> messageConsumers, 
			TeamsConversations teamsConversations, 
			TeamsHTMLParser parser,
			FormConverter formConverter) {
		super();
		this.messageConsumers = messageConsumers;
		this.teamsConversations = teamsConversations;
		this.messageParser = parser;
		this.formConverter = formConverter;
	}

	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
		Activity a = turnContext.getActivity();
		
		if (a.getValue() != null) {
			return processForm(turnContext, a);
		} else {
			return processMessage(turnContext, a);
		}
	}

	protected CompletableFuture<Void> processForm(TurnContext turnContext, Activity a) {
		// TODO Auto-generated method stub
		return null
	}

	protected CompletableFuture<Void> processMessage(TurnContext turnContext, Activity a) {
		Message message = createMessageFromActivity(a);
		TeamsChannelData tcd = a.teamsGetChannelData();
		Object data = a.getChannelData();	
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
