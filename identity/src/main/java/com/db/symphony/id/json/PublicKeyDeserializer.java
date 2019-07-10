package com.db.symphony.id.json;

import java.io.IOException;
import java.security.PublicKey;

import com.db.symphony.id.PemSymphonyIdentity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PublicKeyDeserializer extends StdDeserializer<PublicKey> {

	public PublicKeyDeserializer() {
		this(null);
	}
	
	public PublicKeyDeserializer(Class<PublicKey> vc) {
		super(vc);
	}

	@Override
	public PublicKey deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return PemSymphonyIdentity.createPublicKeyFromString(p.getValueAsString().replace("\\n", "\n"));
	}

}
