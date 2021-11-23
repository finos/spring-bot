package org.finos.springbot.teams.data;

import org.finos.springbot.teams.TeamsException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractDataTransport<X> implements DataTransport<X> {
	
	protected final ObjectMapper om;
	private final String urlPrefix;
	
	public static final String DATA_TRANSPORT_ID="--data";
	
	public AbstractDataTransport(ObjectMapper attachmentDataMapper, String urlPrefix) {
		super();
		this.om = attachmentDataMapper;
		this.urlPrefix = urlPrefix;
	}

	@Override
	public X retrieveFromCard(JsonNode adaptiveCard) {
		JsonNode body = adaptiveCard.get("body");
		if (body.isArray()) {
			for (JsonNode item : body) { 
				JsonNode id = item.get("id");
				if ((id != null) && DATA_TRANSPORT_ID.equals(id.asText())) {
					// ok, found the right element
					JsonNode text = item.get("text");
					return convertToMap(text.asText());
				}
			}
		}
		
		throw new TeamsException("Data not found in adaptiveCard");
	}

	protected abstract X convertToMap(String asText) throws TeamsException;
	
	@Override
	public X retrieveFromCard(String adaptiveCard) {
		try {
			return retrieveFromCard(om.readTree(adaptiveCard));
		} catch (Exception e) {
			throw new TeamsException("Cannot parse adaptive card\n"+adaptiveCard, e);
		}
	}

	@Override
	public X retrieveFromXML(String xmlBody) {
		// quick-and dirty way to find the data payload in the xml
		int idx = xmlBody.indexOf(urlPrefix);
		if (idx != -1) {
			String substr = xmlBody.substring(idx + urlPrefix.length());
			int fin = substr.indexOf("\"");
			if (fin != -1) {
				
			}
		}
		
		throw new TeamsException("Data not found in xml: \n"+xmlBody);
	}

	@Override
	public void introduceIntoCard(JsonNode adaptiveCard, X data) {
		JsonNode body = adaptiveCard.get("body");
		JsonNode dataNode = om.valueToTree(data);
		if (body.isArray()) {
			ObjectNode on = jf.c
			item.v
			
			
		}
		
		throw new TeamsException("Adaptive Card body missing / wrong format");
	}

	@Override
	public void introduceIntoXML(String xml, X data) {
		// TODO Auto-generated method stub
		
	}

}
