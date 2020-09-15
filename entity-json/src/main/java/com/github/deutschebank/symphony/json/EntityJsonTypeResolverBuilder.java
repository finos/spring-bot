package com.github.deutschebank.symphony.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.type.WritableTypeId.Inclusion;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.deutschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;

/**
 * Contains configuration code to get Jackson to read/write in a Symphony-compatible JSON format.
 * 
 * @author Rob Moffat
 *
 */
public class EntityJsonTypeResolverBuilder extends DefaultTypeResolverBuilder {
	
	private VersionSpace[] allowed;
	
	/**
	 * This declares a package prefix, and the versions it supports for writing and reading.
	 * 
	 * This means we can correctly set the "version" : "xxx" part of the JSON format for any given class, 
	 * and also makes sure that we can read the versions provided.
	 * 
	 * 
	 * @author Rob Moffat
	 *
	 */
	public static class VersionSpace {
		
		public final String packagePrefix;
		public final String writeVersion;
		private String[] readVersions;
		
		public VersionSpace(String packagePrefix, String writeVersion, String... readVersions) {
			super();
			this.packagePrefix = packagePrefix;
			this.writeVersion = writeVersion;
			this.readVersions = readVersions;
		}
		
		public Predicate<String> toPattern(String version) {
			String converted = version
				.replace(".", "\\.")
				.replace("*", "[0-9]+");
			return Pattern.compile(converted).asPredicate();
		}
		
		public boolean matches(String in) {
			return writeVersion.equals(in) || Arrays.stream(readVersions).anyMatch(x -> toPattern(x).test(in));
		}
		
		public String getVersions() {
			return writeVersion+ ", "+Arrays.stream(readVersions).reduce("", (a, b) -> a+", "+b);
		}

		@Override
		public String toString() {
			return "VersionSpace [packagePrefix=" + packagePrefix + ", getVersions()=" + getVersions() + "]";
		}
		
	}
	
	public EntityJsonTypeResolverBuilder(TypeFactory typeFactory, VersionSpace... allowed) {
		super(DefaultTyping.JAVA_LANG_OBJECT, new PolymorphicTypeValidator.Base() {

			@Override
			public Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
				return Validity.ALLOWED;
			}

			@Override
			public Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName)
					throws JsonMappingException {
				for (VersionSpace versionSpace : allowed) {
					if (subClassName.startsWith(versionSpace.packagePrefix)) {
						return Validity.ALLOWED;
					}
				}
				
				return Validity.DENIED;
			}

			@Override
			public Validity validateSubType(MapperConfig<?> config, JavaType baseType, JavaType subType)
					throws JsonMappingException {
				return Validity.ALLOWED;
			}
			
		});

		this.inclusion(As.PROPERTY);

		this.init(Id.NAME, new ClassNameIdResolver(typeFactory.constructType(Object.class), typeFactory, _subtypeValidator) {

			@Override
			public String idFromValue(Object value) {
				StringBuilder manipulated = new StringBuilder(value.getClass().getCanonicalName());
				int idx = manipulated.lastIndexOf(".");
				manipulated.setCharAt(idx+1, Character.toLowerCase(manipulated.charAt(idx+1)));
				String id = manipulated.toString();
				return id;
			}

			@Override
			public JavaType typeFromId(DatabindContext context, String id) throws IOException {
				// upper-case the first letter after the last dot
				StringBuilder manipulated = new StringBuilder(id);
				int idx = manipulated.lastIndexOf(".");
				manipulated.setCharAt(idx+1, Character.toUpperCase(manipulated.charAt(idx+1)));
				id = manipulated.toString();
				
				return super.typeFromId(context, id);
			}
			
			
			
		});
		
		this._typeProperty = "type";
		this.allowed = allowed;

		
		
	}
	
	public DeserializationProblemHandler getVersionHandler() {
		return new DeserializationProblemHandler() {

			@Override
			public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p,
					JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
				
				if ("version".equals(propertyName)) {
					String versionNumber = ctxt.readValue(p, String.class);
					String className = beanOrClass.getClass().getCanonicalName();
					
					for (VersionSpace versionSpace : allowed) {
						if (className.startsWith(versionSpace.packagePrefix)) {
							
							if (versionSpace.matches(versionNumber)) {
								// ok
								return true;
							} else {
								throw JsonMappingException.from(p, 
										"Version of object "+className+
										" was "+versionNumber+
										" but versionSpace "+versionSpace.packagePrefix+
										" requires "+versionSpace.getVersions());
							}
						}
					}
					
					return true;	// probably won't get here,
				} else if ("type".equals(propertyName)) {
					// sometimes types are provided even when our objects know what they are.
					// in those cases, just ignore type.
					ctxt.readValue(p, String.class);
					return true;
				} else {
					return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
				}
			}
		};
	}

	@Override
	public boolean useForType(JavaType t) {
		boolean out = super.useForType(t);
		
		if (t.isEnumType()) {
			return false;
		}
		
		if (!out) {
			if (t.isEnumType()) {
				return false;
			}
			
			String className = t.getRawClass().getCanonicalName();
			for (VersionSpace versionSpace : allowed) {
				if (className.startsWith(versionSpace.packagePrefix)) {
					return true;
				}
			}
		}
		
		return out;
	}

	@Override
	public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
		
		if (useForType(baseType)) {
			return new AsPropertyTypeSerializer(_customIdResolver, null, _typeProperty) {

				@Override
				public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
					String version = null;
					Class<? extends Object> class1 = idMetadata.forValue.getClass();
					String className = class1.getCanonicalName();
					for (VersionSpace versionSpace : allowed) {
						if (className.startsWith(versionSpace.packagePrefix)) {
							version= versionSpace.writeVersion;
							break;
						}
					}
					
					if ((version == null) && (class1.isAssignableFrom(EntityJson.class))){
						idMetadata.include = Inclusion.PAYLOAD_PROPERTY;
					}
							
					WritableTypeId out = super.writeTypePrefix(g, idMetadata);
					
					if ((version != null) && (version.trim().length() > 0)) {
						g.writeFieldName("version");
						g.writeString(version);
					}
					
					return out;
				}
			};
		} else {
			return null;
		}
	}

	
	
}
