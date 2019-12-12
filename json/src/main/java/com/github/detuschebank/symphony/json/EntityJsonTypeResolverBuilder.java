package com.github.detuschebank.symphony.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class EntityJsonTypeResolverBuilder extends DefaultTypeResolverBuilder {
	
	private VersionSpace[] allowed;
	
	public static class VersionSpace {
		
		public final String packagePrefix;
		public final String version;
		
		public VersionSpace(String packagePrefix, String version) {
			super();
			this.packagePrefix = packagePrefix;
			this.version = version;
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
				// TODO Auto-generated method stub
				return super.idFromValue(value);
			}

			@Override
			public String idFromValueAndType(Object value, Class<?> type) {
				// TODO Auto-generated method stub
				return super.idFromValueAndType(value, type);
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
							
							if (versionSpace.version.equals(versionNumber)) {
								// ok
								return true;
							} else {
								throw JsonMappingException.from(p, 
										"Version of object "+className+
										" was "+versionNumber+
										" but versionSpace "+versionSpace.packagePrefix+
										" requires "+versionSpace.version);
							}
						}
					}
					
					return true;	// probably won't get here,
				} else if ("type".equals(propertyName)) {
					// sometimes types are provided even when our objects know what they are.
					// in those cases, just ignore type.
					String typeName = ctxt.readValue(p, String.class);
					return true;
				} else {
					return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
				}
			}
		};
	}

}
