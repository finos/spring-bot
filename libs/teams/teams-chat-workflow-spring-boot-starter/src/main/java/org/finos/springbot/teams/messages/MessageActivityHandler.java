package org.finos.springbot.teams.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.content.TeamsHTMLParser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.FormConverter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;

public class MessageActivityHandler extends ActivityHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	TeamsHTMLParser messageParser;
	List<ActionConsumer> messageConsumers;
	TeamsConversations teamsConversations;
	FormConverter formConverter;
	FormValidationProcessor validationProcessor;
	
	public MessageActivityHandler(
			List<ActionConsumer> messageConsumers, 
			TeamsConversations teamsConversations, 
			TeamsHTMLParser parser,
			FormConverter formConverter,
			FormValidationProcessor validationProcessor) {
		super();
		this.messageConsumers = messageConsumers;
		this.teamsConversations = teamsConversations;
		this.messageParser = parser;
		this.formConverter = formConverter;
		this.validationProcessor = validationProcessor;
	}

	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
		try {
			Activity a = turnContext.getActivity();
			
			Action action = (a.getValue() != null) ? processForm(turnContext, a) : 
				processMessage(turnContext, a);
			
			if (action != null) {
				CurrentTurnContext.CURRENT_CONTEXT.set(turnContext);
				
				try {
					Action.CURRENT_ACTION.set(action);
					for (ActionConsumer c : messageConsumers) {
						c.accept(action);
					}
				} finally {
					Action.CURRENT_ACTION.set(Action.NULL_ACTION);
				}
			}
		} catch (Exception e) {
			LOG.error("Couldn't handle event "+turnContext, e);
		} finally {
			CurrentTurnContext.CURRENT_CONTEXT.set(null);
		}

		// errors are handled using Spring's ErrorHandler rather than this.
		return new CompletableFuture<Void>();
	}

	protected FormAction processForm(TurnContext turnContext, Activity a) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Map<String, Object> formData = (Map<String, Object>) a.getValue();
		String formName = (String) formData.remove("form");
		
		Object form = formConverter.convert(formData, formName);
		String action = (String) formData.get("action");
		Map<String, Object> data = new HashMap<>(); // need to load this from somewhere.
		Addressable rr = teamsConversations.getTeamsChat(turnContext);
		User u = teamsConversations.getUser(a.getFrom());
		Addressable from = rr == null ? u : rr;
		return validationProcessor.validationCheck(action, from, form, () -> {
			return new FormAction(from, u, form, action, data);
		});
	}

	protected SimpleMessageAction processMessage(TurnContext turnContext, Activity a) {
		Message message = createMessageFromActivity(a);
		Object data = a.getChannelData();	
		Addressable rr = teamsConversations.getTeamsChat(turnContext);
		User u = teamsConversations.getUser(a.getFrom());
		
		rr = rr == null ? u : rr;
		SimpleMessageAction sma = new SimpleMessageAction(rr, u, message, data);
		
		return sma;
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
