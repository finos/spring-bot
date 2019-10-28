package com.symphony.api.id;


import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * Loads a Symphony identity from a P12 (PKCS12) file containing an RSA private key and certificates.
 * 
 * Although a p12 file could potentially store multiple identities, this will throw an 
 * {@link IdentityConfigurationException} when constructed with a p12 containing multiple aliases.
 */
public class P12SymphonyIdentity extends SingleSymphonyIdentity {

	public P12SymphonyIdentity(X509KeyManager x509KeyManager, String email) {
		super(x509KeyManager, email, getClientAlias(x509KeyManager));
	}


	private static String getClientAlias(X509KeyManager x509KeyManager) {
		String[] out = x509KeyManager.getClientAliases("RSA", new Principal[] {});
		if (out.length != 1) {
			throw new IdentityConfigurationException("Was expecting a single alias in p12, but found "+Arrays.toString(out), null);
		} else {
			return out[0];
		}
	}


	public P12SymphonyIdentity(KeyManagerFactory kmf, String email) {
		this((X509KeyManager) kmf.getKeyManagers()[0], email);
	}

	
	public P12SymphonyIdentity(KeyStore ks, String password, String email) {
		this(createKeyManagerFactory(ks, password), email);
	}
	
	public static KeyManagerFactory createKeyManagerFactory(KeyStore ks, String password) {
		try {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(ks, password.toCharArray());
			return keyManagerFactory;
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't build key manager factory", e);
		}
	}
	
	public P12SymphonyIdentity(InputStream p12inputStream, String password, String email) {
		this(createKeyStore(p12inputStream, password),password, email);
	}

	public static KeyStore createKeyStore(InputStream is, String password) {
		try {
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(is, password.toCharArray());
			return keystore;
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't load keystore", e);
		}
	}


}
