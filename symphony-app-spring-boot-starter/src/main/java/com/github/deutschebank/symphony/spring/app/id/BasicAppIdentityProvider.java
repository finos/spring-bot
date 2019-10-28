package com.github.deutschebank.symphony.spring.app.id;

import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.spring.api.properties.IdentityProperties;
import com.github.deutschebank.symphony.spring.api.properties.IdentityProperties.Type;
import com.github.deutschebank.symphony.spring.app.SymphonyAppProperties;
import com.symphony.api.id.IdentityConfigurationException;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Loads the application identity from a file / properties.
 */
public class BasicAppIdentityProvider {

	public static final String JSON = ".json";
	private SymphonyAppProperties p;
	protected ResourceLoader loader;
	protected ObjectMapper om;

	public BasicAppIdentityProvider(SymphonyAppProperties p, ResourceLoader loader, ObjectMapper om) {
		this.p = p;
		this.loader = loader;
		this.om = om;
	}
	
	public static String getAppId(SymphonyAppProperties p) {
		if ((p.getIdentity() == null) || (p.getIdentity().getCommonName() == null)) {
			return "noAppId";
		} else {
			return p.getIdentity().getCommonName();
		}
	}

	public SymphonyIdentity getIdentity() throws Exception {
		SymphonyIdentity out = null;
		
		IdentityProperties identity = p.getIdentity();
		String appId = getAppId(p);
		
		if (identity != null) {
			out = performIdentityLoad(identity, appId);
		}
		
		if (out == null) {
			out = performIdentityLoad(getClasspathResourceLocation(appId), appId);
		}

		if (out == null) {
			out = performIdentityLoad(getFileResourceLocation(appId), appId);
		}

		if (out == null) {
			throw new IdentityConfigurationException("Couldn't load app identity", null);
		}
		
		return out;
	}

	protected SymphonyIdentity performIdentityLoad(IdentityProperties identity, String appId) throws Exception {
		return IdentityProperties.instantiateIdentityFromDetails(loader, identity, om);
	}

	/**
	 * Puts the file in the working directory.
	 */
	protected IdentityProperties getFileResourceLocation(String appId) {
		IdentityProperties p = new IdentityProperties();
		p.setType(Type.JSON);
		p.setLocation( "file:./"+appId + JSON);
		return p;
	}

	protected IdentityProperties getClasspathResourceLocation(String appId) {
		IdentityProperties p = new IdentityProperties();
		p.setType(Type.JSON);
		p.setLocation( "classpath:/ssl/" + appId + JSON);
		return p;
	}

}
