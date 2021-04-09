package org.finos.symphony.webhookbot.domain;

import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.webhookbot.Helpers;

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
}
