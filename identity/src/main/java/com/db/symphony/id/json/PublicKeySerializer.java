package com.db.symphony.id.json;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PublicKeySerializer extends StdSerializer<PublicKey> {

	public PublicKeySerializer(Class<PublicKey> t) {
		super(t);
	}
	
	public PublicKeySerializer() {
		this(null);
	}
	

	@Override
	public void serialize(PublicKey value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString("-----BEGIN PUBLIC KEY-----\n"+
				Base64.getEncoder().encodeToString(value.getEncoded())+
				"\n-----END PUBLIC KEY-----");
	}

}
