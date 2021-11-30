package org.finos.springbot.entityjson;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
import com.fasterxml.jackson.databind.JsonNode;
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

/**
 * Contains configuration code to get Jackson to read/write in a Symphony-compatible JSON format.
 * 
 * @author Rob Moffat
 *
 */
@SuppressWarnings("serial")
public class EntityJsonTypeResolverBuilder extends DefaultTypeResolverBuilder {
	
	private List<VersionSpace> allowed;
	
	public void addVersionSpace(VersionSpace vs) {
		for (VersionSpace v: allowed) {
			if (v.getToUse().equals(vs.getToUse())) {
				if (!v.equals(vs)) {
					throw new IllegalArgumentException("Version Space already contains "+vs.getToUse());
				}
			}
		}
		
		allowed.add(vs);
	}
	
	public EntityJsonTypeResolverBuilder(TypeFactory typeFactory, List<VersionSpace> allowed) {
		super(DefaultTyping.JAVA_LANG_OBJECT, new PolymorphicTypeValidator.Base() {

			@Override
			public Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
				return Validity.ALLOWED;
			}

			@Override
			public Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName)
					throws JsonMappingException {
				for (VersionSpace versionSpace : allowed) {
					if (versionSpace.typeMatches(subClassName)) {
						return Validity.ALLOWED;
					}
				}
				
				if (subClassName.startsWith("java.util.")) {
					// due to https://github.com/finos/symphony-java-toolkit/issues/113 
					return Validity.ALLOWED;
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
		this.allowed = allowed;
		
		this.init(Id.NAME, new ClassNameIdResolver(typeFactory.constructType(Object.class), typeFactory, _subtypeValidator) {

			@Override
			public String idFromValue(Object value) {
				Class<?> c = value.getClass();
				Optional<VersionSpace> vs = allowed.stream()
					.filter(v -> v.getToUse().equals(c))
					.findFirst();
				
				if (vs.isPresent()) {
					return vs.get().typeName;
				} else {
					return EntityJson.getEntityJsonTypeName(c);
				}
			}

			@Override
			public JavaType typeFromId(DatabindContext context, String id) throws IOException {
				Optional<VersionSpace> vs = allowed.stream()
						.filter(v -> v.typeName.equals(id))
						.findFirst();
				
				if (vs.isPresent()) {
					Class<?> theClass = vs.get().getToUse();
 					return super.typeFromId(context, theClass.getName());
				} else if (id.startsWith("java.util.")) {
					// due to https://github.com/finos/symphony-java-toolkit/issues/113 
					// we are going to allow java util classes, since they are part of the
					// jdk and therefore don't represent a security threat.
					String javaName = "java.util."+fixJavaName(id.substring(10));
					return super.typeFromId(context, javaName);
				} else {
//					if (context instanceof DeserializationContext) {
//		                // First: we may have problem handlers that can deal with it?
//		                return ((DeserializationContext) context).handleUnknownTypeId(_baseType, id, this, "no such class found in VersionSpace");
//		            }
//					
//					// apparently shouldn't reach this point
					return null;
				}
			}

			private String fixJavaName(String in) {
				return in.substring(0, 1).toUpperCase()+in.substring(1);
			}
			
			
			
		});
		
		this._typeProperty = "type";
	}
	
	public DeserializationProblemHandler getVersionHandler() {
		return new DeserializationProblemHandler() {

			@Override
			public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p,
					JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
				
				if ("version".equals(propertyName)) {
					String versionNumber = ctxt.readValue(p, String.class);
					
					JsonMappingException jme = null;
					
					for (VersionSpace versionSpace : allowed) {
						if (versionSpace.typeMatches(beanOrClass)) {
							if (versionSpace.versionMatches(versionNumber)) {
								// ok
								return true;
							} else {
								jme = JsonMappingException.from(p, 
										"Version of object "+beanOrClass+
										" was "+versionNumber+
										" but versionSpace "+versionSpace.typeName+
										" requires "+versionSpace.getVersions());
							}
						}
					}
					
					if (jme != null) {
						throw jme;
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

		if (JsonNode.class.isAssignableFrom(t.getRawClass())) {
			return false;
		}
		
		if (!out) {
			for (VersionSpace versionSpace : allowed) {
				if (t.isTypeOrSuperTypeOf(versionSpace.getToUse())) {
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
					for (VersionSpace versionSpace : allowed) {
						if (versionSpace.getToUse().equals(class1)) {
							version= versionSpace.writeVersion;
							
							idMetadata.id = versionSpace.typeName;
							idMetadata.include = Inclusion.METADATA_PROPERTY;
							
							g.writeTypePrefix(idMetadata);
							
							if ((version != null) && (version.trim().length() > 0)) {
								g.writeFieldName("version");
								g.writeString(version);
							}
							
							
							return idMetadata;
						}
					}
					
					if ((version == null) && (class1.isAssignableFrom(EntityJson.class))){
						// this means don't bother adding any type for the EntityJson object.
						idMetadata.include = Inclusion.PAYLOAD_PROPERTY;
					}
					
					WritableTypeId out = super.writeTypePrefix(g, idMetadata);
					return out;
				}
			};
		} else {
			return null;
		}
	}

	
	
}
