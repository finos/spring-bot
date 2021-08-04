package org.finos.symphony.toolkit.workflow.sources.symphony.json;

import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.json.VersionSpace;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.DataHandler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Converts workflow objects to/from JSON.
 */
public class EntityJsonConverter implements DataHandler {

	ObjectMapper om;
	
	public EntityJsonConverter(List<VersionSpace> versions) {
		this(new ObjectMapper(), versions);
	}
	
	public EntityJsonConverter(ObjectMapper objectMapper, List<VersionSpace> classesToUse) {
		om = ObjectMapperFactory.initialize(objectMapper, ObjectMapperFactory.extendedSymphonyVersionSpace(classesToUse));		
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.registerModule(new JavaTimeModule());
	}

	public EntityJson readValue(String json) {
		try {
			if (json == null) {
				return null;
			}
			return om.readValue(json, EntityJson.class);
		} catch (Exception e) {
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