package com.db.symphony.id.json;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CertificateSerializer extends StdSerializer<X509Certificate> {

	public CertificateSerializer(Class<X509Certificate> t) {
		super(t);
	}

	public CertificateSerializer() {
		this(null);
	}

	@Override
	public void serialize(X509Certificate x509Certificate, JsonGenerator gen, SerializerProvider provider) throws IOException {
		try {
				gen.writeString(serializeCertificate(x509Certificate));				
		} catch (CertificateEncodingException e) {
			throw new IOException("Couldn't encode certificate", e);
		}
	}

	public static String serializeCertificate(X509Certificate x509Certificate) throws CertificateEncodingException {
		return "-----BEGIN CERTIFICATE-----\n" + Base64.getEncoder().encodeToString(x509Certificate.getEncoded())
				+ "\n-----END CERTIFICATE-----";
	}

}
