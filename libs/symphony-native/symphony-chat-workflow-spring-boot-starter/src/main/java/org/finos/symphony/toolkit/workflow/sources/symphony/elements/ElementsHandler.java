package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.ErrorMap;
import org.finos.springbot.workflow.form.FormSubmission;
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversations;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4SymphonyElementsAction;

/**
 * Takes an elements event and turns it into a workflow request.
 * 
 * @author Rob Moffat
 *
 */
public class ElementsHandler implements StreamEventConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(ElementsHandler.class);
	
	MessagesApi messagesApi;
	EntityJsonConverter jsonConverter;
	FormConverter formConverter;
	List<ActionConsumer> elementsConsumers;
	SymphonyResponseHandler rh;
	SymphonyConversations ruBuilder;
	Validator v;
	
	public ElementsHandler(MessagesApi messagesApi, EntityJsonConverter jsonConverter,
			FormConverter formConverter, List<ActionConsumer> elementsConsumers, SymphonyResponseHandler rh, SymphonyConversations ruBuilder, Validator v) {
		this.messagesApi = messagesApi;
		this.jsonConverter = jsonConverter;
		this.formConverter = formConverter;
		this.elementsConsumers = elementsConsumers;
		this.rh = rh;
		this.ruBuilder = ruBuilder;
		this.v = v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accept(V4Event t) {
		try {
			V4SymphonyElementsAction action = t.getPayload().getSymphonyElementsAction();
			if (action != null) {
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
				User u = ruBuilder.loadUserById(t.getInitiator().getUser().getUserId());
				
				// if we're not in a room, address the user directly.
				rr = rr == null ? u : rr;
				Errors e = ErrorHelp.createErrorHolder();
				
				if (validated(currentForm, e)) {
					FormAction ea = new FormAction(rr, u, currentForm, verb, data);
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
				} else {
					WorkResponse fr = new WorkResponse(rr, currentForm,  WorkMode.EDIT, 
						ButtonList.of(new Button(verb, Button.Type.ACTION, "Retry")), convertErrorsToMap(e));
					rh.accept(fr);
				}
			}
		} catch (Exception e) {
			LOG.error("Couldn't handle event "+t, e);
		}
	}

	/**
	 * If the button exists on the form in view-mode, there won't be any values in the
	 * submitted data, just an action.
	 */
	private boolean hasFormData(Map<String, Object> formValues) {
		return formValues.size() > 1;
	}

	private boolean validated(Object currentForm, Errors e) {
		if ((currentForm != null) && (!(currentForm instanceof FormSubmission))) {
			v.validate(currentForm, e);
			return !e.hasErrors();
		} else {
			return true;
		}
	}

	public static ErrorMap convertErrorsToMap(Errors e) {
		return e == null ? new ErrorMap() : new ErrorMap(e.getAllErrors().stream()
			.map(err -> (FieldError) err)
			.collect(Collectors.toMap(fe -> fe.getField(), fe -> ""+fe.getDefaultMessage())));
	}

	
	private EntityJson retrieveData(String formMessageId) {
		V4Message originatingMessage = messagesApi.v1MessageIdGet(null, null, formMessageId.replace("/", "_").replace("+", "-").replace("=", ""));
		return jsonConverter.readValue(originatingMessage.getData());
	}


}
