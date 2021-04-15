package org.finos.symphony.webhookbot;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.management.ObjectName;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.form.HeaderDetails;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.webhookbot.domain.WebHook;
import org.finos.symphony.webhookbot.domain.WebhookPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

@Controller
public class ReceiveController {

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
	
	@PostMapping(path = "/hook/{streamId}/{hookId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> receiveWebhook(
			@PathVariable(name = "streamId") String streamId, 
			@PathVariable(name = "hookId") String hookId,
			@RequestBody JsonNode body) throws JsonProcessingException {
		Room r = rooms.loadRoomById(streamId);
		Tag hookTag = new HashTagDef(hookId);
		Optional<WebHook> ho = history.getLastFromHistory(WebHook.class, hookTag, r);
			
		if (ho.isPresent()) {
			WebHook hook = ho.get();
			// ok, we've found the webhook for this call.  
			EntityJson out = createEntityJson(body, hook);
			
			String template = hook.getTemplate();
			if (!StringUtils.hasText(template)) {
				template = createDefaultTemplate(body, ho.get().getDisplayName());
				hook.setTemplate(template);
			}
			
			MessageResponse mr = new MessageResponse(wf, r, out, "" , "", template);
			handler.accept(mr);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
	}

	protected EntityJson createEntityJson(JsonNode body, WebHook webhook) throws JsonProcessingException {
		EntityJson out = new EntityJson();
		WebhookPayload payload = new WebhookPayload();
		payload.setContents(body);
		out.put("payload", payload);
		out.put(EntityJsonConverter.WORKFLOW_001, webhook);
		Set<HashTag> tags = new HashSet<>();
		tags.add(webhook.getHashTag());
		tags.add(webhook.getHookId());
		tags.addAll(TagSupport.classHashTags(WebHook.class));
		HeaderDetails hd = new HeaderDetails(webhook.getDisplayName(), "", tags);
		out.put("header", hd);
		return out;
	}

	private String createDefaultTemplate(JsonNode body, String title) {
		List<String> fields = new ArrayList<String>();
		String prefix = "entity.payload.contents";
		addSomeFields(prefix, body, fields);
		String fieldsTemplate = fields.stream().map(n -> 
				"\n<tr><td><span class=\"tempo-text-color--secondary\">"+n.substring(prefix.length())+"</span></td>\n" + 
				   "<td><span class=\"tempo-bg-color--red tempo-text-color--white tempo-token\">${"+n+"}</span></td></tr>\n")
				.reduce("", String::concat);
		
				
		return "<h4>"+title+"</h4><br /><table>"+fieldsTemplate+"</table>";
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
