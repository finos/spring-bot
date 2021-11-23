package org.finos.springbot.teams.data;

import java.util.Map;

import org.finos.springbot.teams.TeamsException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataTransportImpl extends AbstractDataTransport<Map<String, Object>> {

	public DataTransportImpl(ObjectMapper attachmentDataMapper, String urlPrefix) {
		super(attachmentDataMapper, urlPrefix);
	}

	@Override
	protected Map<String, Object> convertToMap(String asText) throws TeamsException {
		try {
			TypeReference<Map<String, Object>> tr = new TypeReference<Map<String,Object>>() {};
			return om.readValue(asText, tr);
		} catch (Exception e) {
			throw new TeamsException("Couldn't convert to a map: "+asText, e);
		}
	}

}
