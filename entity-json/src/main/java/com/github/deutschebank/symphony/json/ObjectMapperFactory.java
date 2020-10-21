package com.github.deutschebank.symphony.json;

import java.util.Arrays;

import org.symphonyoss.Taxonomy;
import org.symphonyoss.fin.Security;
import org.symphonyoss.fin.security.id.Cusip;
import org.symphonyoss.fin.security.id.Isin;
import org.symphonyoss.fin.security.id.Openfigi;
import org.symphonyoss.fin.security.id.Ticker;
import org.symphonyoss.taxonomy.Hashtag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.symphony.user.Mention;
import com.symphony.user.UserId;

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
	 * The Symphony client itself uses classes like Isin, Ticker, Mention, UserId when reporting hashtags, cashtags etc.
	 * This function provides a default set of VersionSpaces to allow these to be deserialized.
	 */
	public static VersionSpace[] basicSymphonyVersionSpace() {
		return new VersionSpace[] { 
				new VersionSpace(Taxonomy.class.getCanonicalName(), "1.0"),
				new VersionSpace(Security.class.getCanonicalName(), "1.0", "0.*"),
				new VersionSpace(Mention.class.getCanonicalName(), "1.0"), 
				new VersionSpace(UserId.class.getCanonicalName(), "1.0"), 
				new VersionSpace(Hashtag.class.getCanonicalName(), "1.0"), 
				noVersion(Ticker.class), 
				noVersion(Cusip.class), 
				noVersion(Isin.class), 
				noVersion(Openfigi.class),
			};
	}

	/**
	 * Provides all of the classes in the basicSymphonyVersionSpace (above), as well as any you provide in the
	 * varargs.
	 */
	public static VersionSpace[] extendedSymphonyVersionSpace(VersionSpace... second) {
		VersionSpace[] first = basicSymphonyVersionSpace();
		VersionSpace[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * Provides a no-version-number VersionSpace for a given class.
	 */
	public static VersionSpace noVersion(Class<?> class1) {
		return new VersionSpace(class1.getCanonicalName(), "");
	}
}