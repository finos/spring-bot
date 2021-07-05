package org.finos.symphony.toolkit.workflow.sources.symphony.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.ErrorMap;
import org.finos.symphony.toolkit.workflow.form.HeaderDetails;
import org.finos.symphony.toolkit.workflow.form.RoomList;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.RoomDef;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.UserDef;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Converts workflow objects to/from JSON.
 */
public class EntityJsonConverter {

	public static final String WORKFLOW_001 = "workflow_001";

	ObjectMapper om;
	
	public EntityJsonConverter() {
		this(instantiateObjectMapper());
	}

	private static ObjectMapper instantiateObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		return om;
	}
	
	public EntityJsonConverter(ObjectMapper objectMapper) {
		List<Class<?>> extendedClassSpace = new ArrayList<Class<?>>();
		extendedClassSpace.add(RoomDef.class);
		extendedClassSpace.add(UserDef.class);
		extendedClassSpace.add(User.class);
		extendedClassSpace.add(Room.class);
		extendedClassSpace.add(Button.class);
		extendedClassSpace.add(ButtonList.class);
		extendedClassSpace.add(RoomList.class);
		extendedClassSpace.add(ErrorMap.class);
		extendedClassSpace.add(HeaderDetails.class);
		
		extendedClassSpace.addAll(wf.getDataTypes());
		VersionSpace[] vs = extendedClassSpace.stream().map(c -> new VersionSpace(c.getCanonicalName(), "1.0")).toArray(s -> new VersionSpace[s]);
		om = ObjectMapperFactory.initialize(objectMapper, ObjectMapperFactory.extendedSymphonyVersionSpace(vs));		
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

	public String writeValue(EntityJson ej) {
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
}
