package com.db.symphony.id.json;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PrivateKeySerializer extends StdSerializer<PrivateKey> {

	public PrivateKeySerializer(Class<PrivateKey> t) {
		super(t);
	}
	
	public PrivateKeySerializer() {
		this(null);
	}
	

	@Override
	public void serialize(PrivateKey value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString("-----BEGIN PRIVATE KEY-----\n"+
				Base64.getEncoder().encodeToString(value.getEncoded())+
				"\n-----END PRIVATE KEY-----");
	}

}
