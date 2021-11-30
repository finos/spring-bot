package org.finos.springbot.workflow.data;

import java.util.List;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.entityjson.EntityJsonTypeResolverBuilder;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.response.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides a {@link DataHandler} implementation, where the contents are 
 * saved in entity-json format (i.e types labelled) format.
 */
public class EntityJsonConverter implements DataHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(EntityJsonConverter.class);

	ObjectMapper om;
	EntityJsonTypeResolverBuilder ejtsb;
	
	public EntityJsonConverter(ObjectMapper om, List<VersionSpace> initial) {
		ejtsb = new EntityJsonTypeResolverBuilder(om.getTypeFactory(), initial);
		om.setDefaultTyping(ejtsb);
		om.addHandler(ejtsb.getVersionHandler());
		this.om = om;
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
	
	public void addVersionSpace(VersionSpace vs) {
		ejtsb.addVersionSpace(vs);
	}

	@Override
	public String formatData(DataResponse dr) {
		return writeValue(dr.getData());
	}
}
