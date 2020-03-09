package com.symphony.api.bindings.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * This is to fix https://github.com/deutschebank/symphony-java-client-parent/issues/33
 * in which Symphony add extra content into the JSON messages in a release without warning.
 * 
 * It will allow Jersey/CXF to ignore extra attributes and continue processing.
 * 
 * @author robmoffat
 *
 */
public class LenientJacksonJsonProvider extends JacksonJsonProvider{

	public LenientJacksonJsonProvider() {
		super();
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

}
