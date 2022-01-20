package org.finos.springbot.symphony.form;

import java.util.List;
import java.util.Map;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.form.FormConverter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.spring.events.RealTimeEvent;

/**
 * Takes an elements event and turns it into a workflow request.
 * 
 * @author Rob Moffat
 *
 */
public class ElementsHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ElementsHandler.class);
	
	MessageService messagesApi;
	EntityJsonConverter jsonConverter;
	FormConverter formConverter;
	List<ActionConsumer> elementsConsumers;
	SymphonyConversations ruBuilder;
	FormValidationProcessor validationProcessor;
	
	public ElementsHandler(MessageService messagesApi, EntityJsonConverter jsonConverter,
			FormConverter formConverter, List<ActionConsumer> elementsConsumers, SymphonyConversations ruBuilder, FormValidationProcessor fvp) {
		this.messagesApi = messagesApi;
		this.jsonConverter = jsonConverter;
		this.formConverter = formConverter;
		this.elementsConsumers = elementsConsumers;
		this.ruBuilder = ruBuilder;
		this.validationProcessor = fvp;
	}

	@SuppressWarnings("unchecked")
	@EventListener
	public void accept(RealTimeEvent<V4SymphonyElementsAction> event) {
		try {
			V4SymphonyElementsAction action = event.getSource();
			Map<String, Object> formValues = (Map<String, Object>) action.getFormValues();
			String verb = (String) formValues.get("action");
			String formId = action.getFormId();
			
			Object currentForm;
			if (hasFormData(formValues)) {
				currentForm = formConverter.convert(formValues, formId);
			} else {
				currentForm = null;
			}
			
			EntityJson data = retrieveData(action.getFormMessageId());
			Addressable rr = ruBuilder.loadRoomById(action.getStream().getStreamId());
			User u = ruBuilder.loadUserById(event.getInitiator().getUser().getUserId());
			
			// if we're not in a room, address the user directly.
			Addressable from = rr == null ? u : rr;
			
			FormAction ea = validationProcessor.validationCheck(verb, rr, currentForm, () -> {
				return new FormAction(from, u, currentForm, verb, data);
			});
			
			if (ea != null) {
				try {
					Action.CURRENT_ACTION.set(ea);
					for (ActionConsumer c : elementsConsumers) {
						try {
							c.accept(ea);
						} catch (Exception ee) {
							LOG.error("Failed to handle consumer "+c, ee);
						}
					}
				} finally {
					Action.CURRENT_ACTION.set(Action.NULL_ACTION);
				}	
			};
		} catch (Exception e) {
			LOG.error("Couldn't handle event "+event, e);
		}
	}

	/**
	 * If the button exists on the form in view-mode, there won't be any values in the
	 * submitted data, just an action.
	 */
	private boolean hasFormData(Map<String, Object> formValues) {
		return formValues.size() > 1;
	}
	
	private EntityJson retrieveData(String formMessageId) {
		if (formMessageId != null) {
			V4Message originatingMessage = messagesApi.getMessage(formMessageId.replace("/", "_").replace("+", "-").replace("=", ""));
			return jsonConverter.readValue(originatingMessage.getData());
		} else {
			return new EntityJson();
		}
	}


}
