package com.symphony.api.id.json;

import java.io.IOException;
import java.security.PublicKey;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.symphony.api.id.PemSymphonyIdentity;

public class PublicKeyDeserializer extends StdDeserializer<PublicKey> {

	private static final long serialVersionUID = -3111361597483759505L;

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
