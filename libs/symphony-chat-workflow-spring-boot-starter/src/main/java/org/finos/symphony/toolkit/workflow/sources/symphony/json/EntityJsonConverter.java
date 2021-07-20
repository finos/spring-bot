package org.finos.symphony.toolkit.workflow.sources.symphony.json;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
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

	public static final String WORKFLOW_001 = "workflow_001";

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
		//om.registerModule(new SymphonyModule());
		
	}

	public Object readWorkflowValue(String json) {
		try {
			if (json == null) {
				return null;
			}

			return readWorkflow(readValue(json));
		} catch (Exception e) {
			System.out.println(json);
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}
	
	public Object readWorkflow(EntityJson ej) {
		return ej == null ? null : ej.get(WORKFLOW_001);
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

	
	public static EntityJson newWorkflow(Object o) {
		return new EntityJson(Collections.singletonMap(WORKFLOW_001, o));
	}


	/** 
	 * Used in tests 
	 */
	public String toWorkflowJson(Object o) {
		try {
			if (o == null) {
				return null;
			}
			EntityJson out = new EntityJson();
			out.put(WORKFLOW_001, o);
			return om.writeValueAsString(out);
		} catch (Exception e) {
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}

	@Override
	public String formatData(DataResponse dr) {
		return writeValue(dr.getData());
	}
}
