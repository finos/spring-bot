package com.db.symphony.id.json;

import java.io.IOException;
import java.security.PrivateKey;

import com.db.symphony.id.PemSymphonyIdentity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PrivateKeyDeserializer extends StdDeserializer<PrivateKey> {

	public PrivateKeyDeserializer() {
		this(null);
	}
	
	public PrivateKeyDeserializer(Class<PrivateKey> vc) {
		super(vc);
	}

	@Override
	public PrivateKey deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return PemSymphonyIdentity.createPrivateKeyFromString(p.getValueAsString().replace("\\n", "\n"));
	}

}
