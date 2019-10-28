package com.symphony.spring.app.id;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.id.SingleSymphonyIdentity;
import com.symphony.id.SymphonyIdentity;
import com.symphony.spring.api.properties.IdentityProperties;
import com.symphony.spring.app.SymphonyAppProperties;

/**
 * Creates a Symphony identity that we can use with a given app id.
 * 
 * @author Rob Moffat
 *
 */
public class GeneratingAppIdentityProvider extends BasicAppIdentityProvider {
	
	private static Logger LOG = LoggerFactory.getLogger(GeneratingAppIdentityProvider.class);
	
	CertificateTools certTools;

	
	public GeneratingAppIdentityProvider(SymphonyAppProperties p, ResourceLoader loader, ObjectMapper om) {
		this(p, loader, om, new BCCertificateTools());
	}
		
	public GeneratingAppIdentityProvider(SymphonyAppProperties p, ResourceLoader loader, ObjectMapper om, CertificateTools tools) {
		super(p, loader, om);
		this.certTools = tools;
	}
	
	/**
	 * Augments the identity process by generating a new self-signed certificate
	 * and storing it in the application directory.
	 * 
	 * Note - terrible idea if running with multiple instances and not sharing the f/s.
	 */
	@Override
	protected SymphonyIdentity performIdentityLoad(IdentityProperties identity, String appId) throws Exception {
		SymphonyIdentity out = super.performIdentityLoad(identity, appId);

		if (out == null) {
			String location = identity.getLocation();
			Resource r = loader.getResource(location);
			if (r.isFile()) {
				LOG.info("Creating a new identity in {}, since one couldn't be loaded", location);
				
				KeyPair keyPair = certTools.createKeyPair();
			    X509Certificate cert = certTools.createSelfSignedCertificate(appId, keyPair);
			    out = new SingleSymphonyIdentity((RSAPrivateCrtKey) keyPair.getPrivate(), null, new X509Certificate[] { cert }, appId);
			    OutputStream os = new FileOutputStream(r.getFile());
			    om.writeValue(os, out);
			    os.close();
			} else {
				
			}
		}

		return out;
	}

}
