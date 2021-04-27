package org.finos.symphony.webhookbot.domain;

import java.util.Optional;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.webhookbot.Helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebHookOps {

	public static final String TEMPLATE_UPDATE = "template-update";

	@Exposed(addToHelp = true, isButton = false, description =  "Create a New Webhook receiver. e.g.  <b>/newhook #somesubjecthashtag Your Hook Title</b>", isMessage = true)
	public static ActiveWebHooks newhook(Workflow wf, HashTag ht, Message m, Helpers h, Room r, Addressable a, SymphonyRooms sr, SymphonyHistory hist, ResponseHandler rh) {
		ActiveWebHooks webhooks = hist.getLastFromHistory(ActiveWebHooks.class, a).orElse(new ActiveWebHooks());

		String display = m 
				.without(Word.of("newhook"))
				.without(Word.of("/newhook"))
				.only(Word.class)
				.stream()
				.map(w -> w.getText()+ " ")
				.reduce("", String::concat)
				.trim();
		
		String hookId = h.createHookId(ht.getName(), display);
		
		for (WebHook w : webhooks.getWebhooks()) {
			if (w.getHashTag().equals(ht)) {
				rh.accept(new ErrorResponse(wf, a, "A webhook with this tag already exists: "+ht.getId()));
				return webhooks;
			}
		}
		
		String streamId = r == null ? sr.getStreamFor(a) : sr.getStreamFor(r);
		String url = h.createHookUrl(streamId, hookId);
		
		WebHook out = new WebHook();
		out.setDisplayName(display);
		out.setHookId(new HashTagDef(hookId));
		out.setHashTag(ht);
		out.setUrl(url);
		
		webhooks.getWebhooks().add(out);
		
		return webhooks;
	}
	
	@Exposed(description = "Create an attachment of the last run webhook payload.  e.g. <b>/payload</b>")
	public static Response payload(Workflow wf, Addressable a, History h, ObjectMapper om) throws JsonProcessingException {
		Optional<WebHook> wh = h.getLastFromHistory(WebHook.class, a);
		if (wh.isEmpty()) {
			return new ErrorResponse(wf, a, "No webhooks defined");
		}
		Optional<WebhookPayload> hook = h.getLastFromHistory(WebhookPayload.class, wh.get().getHookId(), a);
		if (hook.isPresent()) {
			WebhookPayload th = hook.get();
			byte[] contents = om.writerWithDefaultPrettyPrinter().writeValueAsBytes(th.getContents());
			return new AttachmentResponse(wf, a, null, wh.get().getDisplayName(), "", contents, ".txt");
		} else {
			return new ErrorResponse(wf, a, "Couldn't find this webhook in the room - does it exist?  Has it been called before?");
		}
	}
	
	@Exposed(description = "Show webhooks configured in this room", addToHelp = true, isButton = true, isMessage = true)
	public static ActiveWebHooks list(Workflow wf, Addressable a, History h) {
		ActiveWebHooks webhooks = h.getLastFromHistory(ActiveWebHooks.class, a).orElse(new ActiveWebHooks());
		return webhooks;
	}
	
	@Exposed(description = "Change the template used by the last called webhook", addToHelp = true, isButton = true, isMessage = true) 
	public static Response template(SymphonyHistory hist, Addressable a, Workflow wf) {
		Optional<EntityJson> ej = hist.getLastEntityJsonFromHistory(ActiveWebHooks.class, a);
		if (ej.isEmpty()) {
			return new ErrorResponse(wf, a, "No webhooks defined");
		}
		
		Optional<WebHook> activeHook = hist.getFromEntityJson(ej.get(), WebHook.class);
		if (activeHook.isEmpty()) {
			return new ErrorResponse(wf, a, "Active Hook not set");
		}
		Template t = activeHook.get().getTemplate();
		ButtonList bl = new ButtonList();
		bl.add(new Button(TEMPLATE_UPDATE, Type.ACTION, "Update Template"));

		return new FormResponse(wf, a, ej.get(), "Template Edit", "Update the template used by "+activeHook.get().getDisplayName(), t, true, bl);

	}
}
