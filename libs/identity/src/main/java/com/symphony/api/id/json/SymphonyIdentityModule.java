package com.symphony.api.id.json;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.symphony.api.id.SymphonyIdentity;

/**
 * This supplies a Jackson module for serializing and deserializing {@link SymphonyIdentity} objects
 * to/from JSON.
 * 
 * @author robmoffat
 *
 */
public class SymphonyIdentityModule extends Module {

	@Override
	public String getModuleName() {
		return "Symphony Identity Module";
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addDeserializers(new Deserializers.Base() {

			@Override
			public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
					BeanDescription beanDesc) throws JsonMappingException {

				if (X509Certificate.class.isAssignableFrom(type.getRawClass())) {
					return new CertificateDeserializer();
				} else if (PrivateKey.class.isAssignableFrom(type.getRawClass())) {
					return new PrivateKeyDeserializer();
				} else if (PublicKey.class.isAssignableFrom(type.getRawClass())) {
					return new PublicKeyDeserializer();
				} else {
					return null;
				}			
			}
		});
		
		context.addSerializers(new Serializers.Base() {
			
			@Override
			public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type,
					BeanDescription beanDesc) {

				if (X509Certificate.class.isAssignableFrom(type.getRawClass())) {
					return new CertificateSerializer();
				} else if (PrivateKey.class.isAssignableFrom(type.getRawClass())) {
					return new PrivateKeySerializer();
				} else if (PublicKey.class.isAssignableFrom(type.getRawClass())) {
					return new PublicKeySerializer();
				} else {
					return null;
				}			
			}
		});
	}

	@Override
	public Version version() {
		return Version.unknownVersion();
	}

}
