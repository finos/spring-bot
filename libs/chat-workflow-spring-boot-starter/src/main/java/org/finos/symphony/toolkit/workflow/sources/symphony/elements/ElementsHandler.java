package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.util.List;
import java.util.Map;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.AbstractNeedsWorkflow;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyEventHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter.UnconvertedContent;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.validation.ErrorHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
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
public class ElementsHandler extends AbstractNeedsWorkflow implements SymphonyEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ElementsHandler.class);
	
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
				EntityJson data = retrieveData(action.getFormMessageId());
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
						ButtonList.of(new Button(verb, Button.Type.ACTION, "Retry")), e);
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

	private EntityJson retrieveData(String formMessageId) {
		V4Message originatingMessage = messagesApi.v1MessageIdGet(null, null, formMessageId.replace("/", "_").replace("+", "-").replace("=", ""));
		return jsonConverter.readValue(originatingMessage.getData());
	}


}
