package com.github.deutschebank.symphony.workflow.sources.symphony.elements;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.github.deutschebank.symphony.workflow.AbstractNeedsWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.SymphonyEventHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.FormConverter.UnconvertedContent;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.ResponseHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.PresentationMLHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.github.deutschebank.symphony.workflow.validation.ErrorHelp;
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
public class ElementsHandler extends AbstractNeedsWorkflow implements SymphonyEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	MessagesApi messagesApi;
	EntityJsonConverter jsonConverter;
	FormConverter formConverter;
	List<ElementsConsumer> elementsConsumers;
	ResponseHandler rh;
	SymphonyRooms ruBuilder;
	Validator v;
	
	public ElementsHandler(Workflow wf, MessagesApi messagesApi, EntityJsonConverter jsonConverter,
			FormConverter formConverter, List<ElementsConsumer> elementsConsumers, ResponseHandler rh, SymphonyRooms ruBuilder, Validator v) {
		super(wf);
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
				String verb = (String) ((Map<String, String>) action.getFormValues()).get("action");
				String formId = action.getFormId();
				Object currentForm = formConverter.convert((Map<String, Object>) action.getFormValues(), formId);
				Object data = retrieveData(action.getFormMessageId());
				Addressable rr = ruBuilder.loadRoomById(action.getStream().getStreamId());
				User u = ruBuilder.loadUserById(t.getInitiator().getUser().getUserId());
				
				// if we're not in a room, address the user directly.
				rr = rr == null ? u : rr;
				Author.CURRENT_AUTHOR.set((Author) u);
				Errors e = ErrorHelp.createErrorHolder();
				
				if (validated(currentForm, e)) {
					ElementsAction ea = new ElementsAction(wf, rr, u, currentForm, verb, data);
					for (ElementsConsumer c : elementsConsumers) {
						try {
							List<Response> ra = c.apply(ea);
							if (ra != null) {
								ra.stream()
								.forEach(r -> rh.accept(r));	
							}
							
						} catch (Exception ee) {
							LOG.error("Failed to handle consumer "+c, ee);
						}
					}
				} else {
					FormResponse fr = new FormResponse(wf, rr, data, "Error In Form", "Please Fix the validation errors below", currentForm, true, 
						Collections.singletonList(new Button(verb, Button.Type.ACTION, "Retry")), e);
					rh.accept(fr);
				}
			}
		} catch (Exception e) {
			LOG.error("Couldn't handle event "+t, e);
		}
	}

	private boolean validated(Object currentForm, Errors e) {
		if ((currentForm != null) && (!(currentForm instanceof UnconvertedContent))) {
			v.validate(currentForm, e);
			return !e.hasErrors();
		} else {
			return true;
		}
	}

	private Object retrieveData(String formMessageId) {
		V4Message originatingMessage = messagesApi.v1MessageIdGet(null, null, formMessageId.replace("/", "_").replace("+", "-").replace("=", ""));
		return jsonConverter.readWorkflowValue(originatingMessage.getData());
	}


}
