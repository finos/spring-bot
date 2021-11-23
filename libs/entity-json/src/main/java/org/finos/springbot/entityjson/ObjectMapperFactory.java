package org.finos.springbot.entityjson;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {

	/**
	 * Constructs a new ObjectMapper and applies the
	 * {@link EntityJsonTypeResolverBuilder} to it with the given
	 * {@link VersionSpace}s.
	 */
	public static ObjectMapper initialize(VersionSpace... allowed) {
		ObjectMapper om = new ObjectMapper();
		return initialize(om, allowed);
	}

	/**
	 * Takes the ObjectMapper and applies the
	 * {@link EntityJsonTypeResolverBuilder} to it with the given
	 * {@link VersionSpace}s.
	 */
	public static ObjectMapper initialize(ObjectMapper om, VersionSpace... allowed) {
		EntityJsonTypeResolverBuilder ejtsb = new EntityJsonTypeResolverBuilder(om.getTypeFactory(), allowed);
		om.setDefaultTyping(ejtsb);
		om.addHandler(ejtsb.getVersionHandler());
		return om;
	}

	/**
	 * Provides a no-version-number VersionSpace for a given class.
	 */
	public static VersionSpace noVersion(Class<?> class1) {
		return new VersionSpace(class1, "");
	}
}