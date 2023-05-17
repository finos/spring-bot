package org.finos.springbot.teams.templating.adaptivecard;

import org.finos.springbot.workflow.response.WorkResponse;

import com.fasterxml.jackson.databind.JsonNode;

public class AdaptiveCardPassthrough {

	private final static String ADAPTIVE_CARD = "AdaptiveCard";
	private final static String ADAPTIVE_CARD_TYPE = "type";

	private final JsonNode jsonNode;

	public AdaptiveCardPassthrough(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public static boolean isAdaptiveCard(WorkResponse wr) {
		if (wr.getFormObject() instanceof AdaptiveCardPassthrough) {
			AdaptiveCardPassthrough passthrough = (AdaptiveCardPassthrough) wr.getFormObject();
			JsonNode node = passthrough.getJsonNode();
			JsonNode adaptiveNode = node.get(ADAPTIVE_CARD_TYPE);
			return adaptiveNode.asText().equals(ADAPTIVE_CARD);
		}
		
		return false;
	}

}
