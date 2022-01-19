package com.symphony.api.id.testing;

import java.io.InputStream;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.IdentityConfigurationException;

public class StreamHelp {

	public static String asString(InputStream is) {
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}	
	}
	
	public static <T> T getProperties(String name, Class<T> c) {
		try {
			ObjectMapper om = new ObjectMapper();
			om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			String property = System.getProperties().getProperty(name);
			property = property.replace('\u00A0',' ');
			return om.readValue(property, c);
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't load test identity " + name, e);
		}

	}
}
