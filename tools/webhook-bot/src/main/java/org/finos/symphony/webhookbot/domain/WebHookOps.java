package org.finos.symphony.webhookbot.domain;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.webhookbot.Helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebHookOps {

	@Exposed(addToHelp = true, isButton = false, description =  "Create a New Webhook receiver. e.g.  <b>/newhook #somesubjecthashtag Your Hook Title</b>", isMessage = true)
	public static WebHook newhook(HashTag ht, Message m, Helpers h, Room r, Author a, SymphonyRooms sr) {
		String display = m
				.without(Word.of("newhook"))
				.without(Word.of("/newhook"))
				.only(Word.class)
				.stream()
				.map(w -> w.getText()+ " ")
				.reduce("", String::concat)
				.trim();
		
		String hookId = h.createHookId(ht.getName(), display);
		String streamId = r == null ? sr.getStreamFor(a) : sr.getStreamFor(r);
		String url = h.createHookUrl(streamId, hookId);
		
		WebHook out = new WebHook();
		out.setDisplayName(display);
		out.setHookId(new HashTagDef(hookId));
		out.setHashTag(ht);
		out.setUrl(url);
		
		return out;
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
			return new AttachmentResponse(wf, a, null, wh.get().getDisplayName(), "", contents, ".json");
		} else {
			return new ErrorResponse(wf, a, "Couldn't find this webhook in the room - does it exist?  Has it been called before?");
		}
	}
}
