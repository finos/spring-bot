package com.symphony.api.id.json;

import java.io.IOException;
import java.security.cert.X509Certificate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.symphony.api.id.PemSymphonyIdentity;

public class CertificateDeserializer extends StdDeserializer<X509Certificate> {

	private static final long serialVersionUID = -3560852312063083210L;

	public CertificateDeserializer() {
		this(null);
	}
	
	public CertificateDeserializer(Class<X509Certificate> vc) {
		super(vc);
	}

	@Override
	public X509Certificate deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

			String s = p.getValueAsString();
			X509Certificate cert = PemSymphonyIdentity.createCertificate(s.replace("\\n", "\n"));
		
		return cert;
	}

}
