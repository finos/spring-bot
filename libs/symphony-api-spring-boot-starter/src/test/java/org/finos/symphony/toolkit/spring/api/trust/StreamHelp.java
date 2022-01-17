package org.finos.symphony.toolkit.spring.api.trust;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamHelp {

	public static <T> T getProperties(String name, Class<T> c) {
		try {
			ObjectMapper om = new ObjectMapper();
			String property = System.getProperties().getProperty(name);
			return om.readValue(property, c);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load test identity " + name, e);
		}

	}
}
