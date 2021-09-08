package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.form.FormSubmission;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Converts a form into an object.
 * 
 * 
 * @author Rob Moffat
 *
 */
public class FormConverter {
	
	private static Logger LOG = LoggerFactory.getLogger(FormConverter.class);
	
	private ObjectMapper om = new ObjectMapper();
	
	
	
	public FormConverter(SymphonyRooms r) {
		super();
		om.registerModule(new SymphonyModule());
		om.registerModule(new JavaTimeModule());
	}

	/**
	 * Takes the flat form response and restructures it into a map/list json-style format.
	 */
	public Object convert(Map<String, Object> formValues, String type) throws ClassNotFoundException {
		Object out = new HashMap<>();
		for (String key : formValues.keySet()) {
			if (!key.equals("action")) {
				Object object = formValues.get(key);
				if (key.endsWith(".")) {
					key = key.substring(0,key.length()-1);
				}
				String[] parts = key.split("\\.");
				out = placeInStructure(out, parts, 0, object, key);
			}
		}
		
		// attempt to cast the result.  
		Class<?> c = null;
		try {
			c = Class.forName(type);
		} catch (Exception e1) {
			LOG.debug("Couldn't convert {} ",formValues, e1);
		}
		try {
			System.out.println(om.writeValueAsString(out));
			return om.convertValue(out, c);
		} catch (Exception e) {
			LOG.debug("Couldn't convert {} ",formValues, e);
		}
		try {
			if(formValues.containsKey("entity.formdata")){
				return om.convertValue(formValues.get("entity.formdata"), c);
			}
		} catch (Exception e) {
			LOG.debug("Couldn't convert primitive {} ",formValues, e);
		}
		
		return new FormSubmission(type, out);
	}

	private Object placeInStructure(Object ctx, String[] parts, int p, Object object, String originalKey) {
		if (p >= parts.length) {
			return object;
		} else {
			String part = parts[p];
			part = part.startsWith("[") ? part.substring(1, part.length()-1) : part;
			Integer idx = safeParseInt(part);
			if (idx != null) {
				// array
				List<Object> l = ensureArray(ctx, idx, originalKey);
				l.set(idx, placeInStructure(l.get(idx), parts, p+1, object, originalKey));
				return l;
			} else {
				Map<String, Object> m = ensureMap(ctx, originalKey);
				m.put(part, placeInStructure(m.get(part), parts, p+1, object, originalKey));
				return m;
			}
		}
	}

	private Integer safeParseInt(String part) {
		try {
			Integer i = Integer.parseInt(part);
			return i;
		} catch (NumberFormatException e) {
			return null;

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Object> ensureArray(Object ctx, int index, String originalKey) {
		if (ctx == null) {
			ctx = new ArrayList<>(index+1);
		}
		if (ctx instanceof ArrayList) {
			while (((List)ctx).size() <= index) {
				((List)ctx).add(null);
			}
			return (List<Object>) ctx;
		} else  {
			throw new UnsupportedOperationException("Clash in "+ctx+" which should be array, "+originalKey);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private Map<String, Object> ensureMap(Object ctx, String originalKey) {
		if (ctx == null) {
			ctx = new LinkedHashMap<>();
		}
		if (ctx instanceof Map) {
			return (Map<String, Object>) ctx;
		} else  {
			throw new UnsupportedOperationException("Clash in "+ctx+" which should be map, "+originalKey);
		}
	}

	public ObjectMapper getObjectMapper() {
		return om;
	}


}
