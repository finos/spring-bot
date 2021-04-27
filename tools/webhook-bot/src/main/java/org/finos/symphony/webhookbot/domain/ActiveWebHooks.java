package org.finos.symphony.webhookbot.domain;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

@Work(editable = true, instructions = "List of webhooks enabled in this room", name="Webhooks")
@Template(view = "classpath:/templates/ActiveWebHooksView.ftl", edit="classpath:/templates/ActiveWebhooksEdit.ftl")
public class ActiveWebHooks {

	List<WebHook> webhooks = new ArrayList<WebHook>();

	public List<WebHook> getWebhooks() {
		return webhooks;
	}

	public void setWebhooks(List<WebHook> webhooks) {
		this.webhooks = webhooks;
	}
	
}
