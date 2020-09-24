package com.github.deutschebank.symphony.workflow.sources.symphony.handlers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.github.deutschebank.symphony.json.ObjectMapperFactory;
import com.github.deutschebank.symphony.workflow.AbstractNeedsWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.UserDef;

/**
 * Converts workflow objects to/from JSON.
 */
public class EntityJsonConverter extends AbstractNeedsWorkflow {

	private static final String WORKFLOW_001 = "workflow_001";

	ObjectMapper om;
	
	public EntityJsonConverter(Workflow wf) {
		this(wf, new ObjectMapper());
	}
	
	public EntityJsonConverter(Workflow wf, ObjectMapper objectMapper) {
		super(wf);
		List<Class<?>> extendedClassSpace = new ArrayList<Class<?>>();
		extendedClassSpace.add(RoomDef.class);
		extendedClassSpace.add(UserDef.class);
		extendedClassSpace.add(User.class);
		extendedClassSpace.add(Room.class);
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

			return readValue(json).get(WORKFLOW_001);
		} catch (Exception e) {
			System.out.println(json);
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}

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


}
