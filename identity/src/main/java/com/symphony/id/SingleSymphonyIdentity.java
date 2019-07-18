package com.symphony.id;

import java.net.Socket;
import java.security.KeyFactory;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.symphony.id.json.CertificateDeserializer;
import com.symphony.id.json.CertificateSerializer;
import com.symphony.id.json.PrivateKeyDeserializer;
import com.symphony.id.json.PrivateKeySerializer;

/**
 * Holds a single symphony identity, using a single {@link KeyManager}.
 * 
 * @author Rob Moffat
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class SingleSymphonyIdentity implements SymphonyIdentity {

	@JsonSerialize(using=PrivateKeySerializer.class)
	@JsonDeserialize(using=PrivateKeyDeserializer.class)
	protected RSAPrivateCrtKey privateKey;
	protected String email;
	protected String commonName;
	
	@JsonSerialize(contentUsing=CertificateSerializer.class)
	@JsonDeserialize(contentUsing=CertificateDeserializer.class)
	protected X509Certificate[] chain;
	
	public SingleSymphonyIdentity() {
		super();
	}
	
	public SingleSymphonyIdentity(RSAPrivateCrtKey privateKey, String email, X509Certificate[] chain, String commonName) {
		super();
		this.privateKey = privateKey;
		this.email = email;
		this.chain = chain;
		this.commonName = commonName;
	}
	
	public SingleSymphonyIdentity(X509KeyManager km, String email, String alias) {
		this((RSAPrivateCrtKey) km.getPrivateKey(alias), email, km.getCertificateChain(alias), getCommonName(km.getCertificateChain(alias)));
	}

	@Override
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() throws Exception {
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey out = keyFactory.generatePublic(publicKeySpec);
		return out;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public KeyManager[] getKeyManagers() {
		return new KeyManager[] { new X509KeyManager() {

			@Override
			public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
				return "ALIAS";
			}

			@Override
			public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
				return "ALIAS";
			}

			@Override
			public X509Certificate[] getCertificateChain(String alias) {
				return chain;
			}

			@Override
			public String[] getClientAliases(String keyType, Principal[] issuers) {
				return new String[] { "ALIAS" };
			}

			@Override
			public PrivateKey getPrivateKey(String alias) {
				return privateKey;
			}

			@Override
			public String[] getServerAliases(String keyType, Principal[] issuers) {
				return new String[] { "ALIAS" };
			}
		}};

	}

	@Override
	public X509Certificate[] getCertificateChain() {
		return chain;
	}

	@Override
	public String getCommonName() {
		return commonName;
	}
	
	public static String getCommonName(X509Certificate[] chain) {
		try {
			if (chain.length > 0) {
				LdapName ldapName = new LdapName(chain[0].getSubjectDN().getName());
				for (Rdn rdn : ldapName.getRdns()) {
					if (rdn.getType().equalsIgnoreCase("CN")) {
						return (String) rdn.getValue();
					}
				}
			}
		} catch (InvalidNameException e) {
			throw new IdentityConfigurationException("Couldn't extract common name: ", e);
		}
		
		return null;
	}
}