package org.finos.symphony.webhookbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.form.HeaderDetails;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.webhookbot.domain.ActiveWebHooks;
import org.finos.symphony.webhookbot.domain.Template;
import org.finos.symphony.webhookbot.domain.WebHook;
import org.finos.symphony.webhookbot.domain.WebhookPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

@Controller
public class ReceiveController {

	public static final String INVOKED_WEBHOOK = "invoked";

	public static final String PAYLOAD = "payload";

	@Autowired
	History history;
	
	@Autowired
	SymphonyRooms rooms;
	
	@Autowired
	ResponseHandler handler;
	
	@Autowired
	Workflow wf;
	
	@Autowired
	ObjectMapper om;
	
	@PostMapping(path = "/hook/{streamId}/{hookId}")
	public ResponseEntity<Void> receiveWebhook(
			@PathVariable(name = "streamId") String streamId, 
			@PathVariable(name = "hookId") String hookId,
			@RequestBody JsonNode body) throws JsonProcessingException {
		Room r = rooms.loadRoomById(streamId);
		ActiveWebHooks ho = history.getLastFromHistory(ActiveWebHooks.class, r).orElse(new ActiveWebHooks());
		WebHook hook = getHook(ho, hookId);	
		
		if ((hook != null) && (hook.isActive())) {
			// ok, we've found the webhook for this call.  
			
			Template template = hook.getTemplate();
			if (template==null) {
				template = createDefaultTemplate(body, hook.getDisplayName());
				hook.setTemplate(template);
			}
			
			EntityJson out = createEntityJson(body, ho, hook);
			MessageResponse mr = new MessageResponse(wf, r, out, "" , "", template.getContents());
			handler.accept(mr);
			return new ResponseEntity<Void>(HttpStatus.OK);
			
		} 
		
		
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	public static WebHook getHook(ActiveWebHooks ho, String hookId) {
		for (WebHook wh : ho.getWebhooks()) {
			if (wh.getHookId().getId().equals(hookId)) {
				return wh;
			}
		}
		
		return null;
	}

	public static EntityJson createEntityJson(JsonNode body, ActiveWebHooks active, WebHook webhook) throws JsonProcessingException {
		WebhookPayload payload = new WebhookPayload();
		payload.setContents(body);
		return createEntityJson(active, webhook, payload);
	}

	public static EntityJson createEntityJson(ActiveWebHooks active, WebHook webhook, WebhookPayload payload) {
		EntityJson out = new EntityJson();
		out.put(PAYLOAD, payload);
		out.put(EntityJsonConverter.WORKFLOW_001, active);
		out.put(INVOKED_WEBHOOK, webhook);
		Set<HashTag> tags = new HashSet<>();
		tags.add(webhook.getHashTag());
		tags.add(webhook.getHookId());
		tags.addAll(TagSupport.classHashTags(WebHook.class));
		tags.addAll(TagSupport.classHashTags(ActiveWebHooks.class));
		HeaderDetails hd = new HeaderDetails(webhook.getDisplayName(), "", tags);
		out.put("header", hd);
		return out;
	}

	private Template createDefaultTemplate(JsonNode body, String title) {
		List<String> fields = new ArrayList<String>();
		String prefix = "entity.payload.contents";
		addSomeFields(prefix, body, fields);
		String fieldsTemplate = fields.stream().map(n -> 
				"\n<tr><td><span class=\"tempo-text-color--secondary\">"+n.substring(prefix.length())+"</span></td>\n" + 
				   "<td><span class=\"tempo-bg-color--red tempo-text-color--white tempo-token\">${"+n+"}</span></td></tr>\n")
				.reduce("", String::concat);
		
		
		Template out = new Template();
		out.setName(title+ " Default Template");
		out.setContents("<table>"+fieldsTemplate+"</table>"+buttonForm());
		out.setShared(false);
				
		return out;
	}
	
	private static Map<String, String> DEFAULT_BUTTONS = new HashMap<>();
	
	static {
		DEFAULT_BUTTONS.put("template", "Change This Template");
		DEFAULT_BUTTONS.put("select", "Select Template");
		DEFAULT_BUTTONS.put("view", "View Webhook");
		DEFAULT_BUTTONS.put("filter", "Add Filter");
	}
 
	private String buttonForm() { 
		return "<form id=\"just-buttons-form\"><p>"
				+ DEFAULT_BUTTONS.entrySet().stream()
						.map(v -> "<button name=\"" + v.getKey() + "\" type=\"action\">" + v.getValue() + "</button>")
						.reduce("", String::concat)
				+ "</p></form> ";

	}

	private void addSomeFields(String path, JsonNode body, List<String> fields) {
		if (fields.size() >= 5) {
			return;
		}
		
		if (body instanceof TextNode) {
			fields.add(path);
		} else if (body instanceof ObjectNode) {
			for (Iterator<Entry<String, JsonNode>> iterator = ((ObjectNode)body).fields(); iterator.hasNext();) {
				Entry<String, JsonNode> val = iterator.next();
				addSomeFields(path+"."+val.getKey(), val.getValue(), fields);
			}
		} else if (body instanceof ArrayNode) {
			for (int i = 0; i < ((ArrayNode)body).size(); i++) {
				addSomeFields(path+"["+i+"]", body.get(i), fields);
			}
		}
	}
}
