package org.finos.symphony.webhookbot.domain;

import org.finos.symphony.toolkit.workflow.java.Work;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Contains whatever gets sent in the webhook call
 * 
 * @author rob@kite9.com
 *
 */
@Work
public class WebhookPayload {

	JsonNode contents;

	public JsonNode getContents() {
		return contents;
	}

	public void setContents(JsonNode contents) {
		this.contents = contents;
	}
}
