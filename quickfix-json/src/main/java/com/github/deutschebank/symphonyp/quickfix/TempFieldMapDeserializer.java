package com.github.deutschebank.symphonyp.quickfix;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;

import quickfix.Session;

public class TempFieldMapDeserializer extends AbstractQuickfixjDeserializer<TempFieldMap> {
	
	public TempFieldMapDeserializer(Session s) {
		super(s, TempFieldMap.class);
	}

	@Override
	public TempFieldMap deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		TempFieldMap tfm = new TempFieldMap();
		fillFields(tfm, p, ctxt);
		return tfm;
	}

	
	
}
