package org.finos.springbot.symphony.json;

import java.util.List;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.response.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Converts workflow objects to/from JSON.
 */
public class EntityJsonConverter implements DataHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(EntityJsonConverter.class);

	ObjectMapper om;
	
	public EntityJsonConverter(List<VersionSpace> versions) {
		this(new ObjectMapper(), versions);
	}
	
	public EntityJsonConverter(ObjectMapper objectMapper, List<VersionSpace> classesToUse) {
		om = ObjectMapperFactory.initialize(objectMapper, DataHandlerCofig.extendedSymphonyVersionSpace(classesToUse));		
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.registerModule(new JavaTimeModule());
		om.registerModule(new LegacyFormatModule());
	}

	public EntityJson readValue(String json) {
		try {
			if (json == null) {
				return null;
			}
			return om.readValue(json, EntityJson.class);
		} catch (Exception e) {
			LOG.error("Couldn't read: {}", json);
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}

	public String writeValue(Object ej) {
		try {
			return ej == null ? null : om.writeValueAsString(ej);
		} catch (Exception e) {
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}

	public Object fromJson(String formId, Object json) {
		Class<?> c;
		try {
			c = Class.forName(formId);
		} catch (ClassNotFoundException e1) {
			throw new UnsupportedOperationException("Couldn't get class: " + formId);
		}
		Object updated = om.convertValue(json, c);
		return updated;
	}

	public ObjectMapper getObjectMapper() {
		return om;
	}

	@Override
	public String formatData(DataResponse dr) {
		return writeValue(dr.getData());
	}
}
