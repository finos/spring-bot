package com.db.symphony.id;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Stores enough details about a Symphony user in order they can log in to 
 * REST Apis.
 */
@JsonDeserialize(as = SingleSymphonyIdentity.class)
public interface SymphonyIdentity {

	public PrivateKey getPrivateKey();

	public PublicKey getPublicKey() throws Exception;
	
	/**
	 * Email of the user.  This is held in configuration, rather than looking it up in Symphony.
	 * Bots generally have email addresses, even if they don't read them.
	 */
	public String getEmail();
	
	/**
	 * Use this method when setting up a Symphony client in order to authenticate using the certificates 
	 * and private key contained in this object.
	 */
	public KeyManager[] getKeyManagers();
	
	/**
	 * Utility method for inspecting the certificate chain that this user will present when logging in.
	 * This will be an empty array if the user uses Private Key login.
	 */
	public X509Certificate[] getCertificateChain();
	
	/**
	 * Common name extracted from the first certificate in the chain, or provided as a string. 
	 */
	public String getCommonName();
	
}
