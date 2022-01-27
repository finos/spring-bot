package org.finos.springbot.teams.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.content.serialization.ParseContext;
import org.finos.springbot.teams.content.serialization.TeamsHTMLParser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.history.StorageIDResponseHandler;
import org.finos.springbot.teams.history.TeamsHistory;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Message;
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
	TeamsHistory teamsHistory;
	FormConverter formConverter;
	FormValidationProcessor validationProcessor;
	
	public MessageActivityHandler(
			List<ActionConsumer> messageConsumers, 
			TeamsConversations teamsConversations, 
			TeamsHistory teamsHistory,
			TeamsHTMLParser parser,
			FormConverter formConverter,
			FormValidationProcessor validationProcessor) {
		super();
		this.messageConsumers = messageConsumers;
		this.teamsConversations = teamsConversations;
		this.teamsHistory = teamsHistory;
		this.messageParser = parser;
		this.formConverter = formConverter;
		this.validationProcessor = validationProcessor;
	}

	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
		try {
			Activity a = turnContext.getActivity();
			
			try {
				CurrentTurnContext.CURRENT_CONTEXT.set(turnContext);
				Action action = (a.getValue() != null) ? processForm(turnContext, a) : processMessage(turnContext, a);
			
				if (action != null) {
					Action.CURRENT_ACTION.set(action);
					for (ActionConsumer c : messageConsumers) {
						c.accept(action);
					}
				}
			} finally {
				Action.CURRENT_ACTION.set(Action.NULL_ACTION);
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
		String messageId = (String) formData.remove(StorageIDResponseHandler.STORAGE_ID_KEY);
		
		Object form = formConverter.convert(formData, formName);
		String action = (String) formData.get("action");
		TeamsAddressable rr = teamsConversations.getTeamsAddressable(turnContext.getActivity().getConversation());
		TeamsUser u = teamsConversations.getUser(a.getFrom());
		TeamsAddressable from = takeUser(rr) ? u : rr;
		Map<String, Object> data = retrieveData(messageId, from);
		return validationProcessor.validationCheck(action, from, form, () -> {
			return new FormAction(from, u, form, action, data);
		});
	}

	private boolean takeUser(TeamsAddressable rr) {
		return (rr instanceof TeamsUser) || (rr==null);
	}

	private Map<String, Object> retrieveData(String messageId, TeamsAddressable ta) {
		return teamsHistory.retrieve(messageId, ta).orElseGet(() -> new EntityJson());
	}

	protected SimpleMessageAction processMessage(TurnContext turnContext, Activity a) {
		Object data = a.getChannelData();	
		TeamsAddressable rr = teamsConversations.getTeamsAddressable(turnContext.getActivity().getConversation());
		TeamsUser u = teamsConversations.getUser(a.getFrom());
		Message message = createMessageFromActivity(a, rr);
		
		rr = takeUser(rr) ? u : rr;
		SimpleMessageAction sma = new SimpleMessageAction(rr, u, message, data);
		
		return sma;
	}

	private Message createMessageFromActivity(Activity a, TeamsAddressable within) {
		if (a.getAttachments() != null) {
			List<Attachment> attachments = new ArrayList<>(a.getAttachments());
			Collections.reverse(attachments);
			for (Attachment attachment : attachments) {
				if (MediaType.TEXT_HTML_VALUE.equals(attachment.getContentType())) {
					ParseContext pc = new ParseContext(within, a.getEntities());
					return messageParser.apply((String) attachment.getContent(), pc);
				}
			}
		}
		
		return Message.of(a.getText());
	}
}
