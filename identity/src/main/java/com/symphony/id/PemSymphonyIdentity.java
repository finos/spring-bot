package com.symphony.id;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/** 
 * Adds logic to allow identity to be loaded in PEM format.
 */
public class PemSymphonyIdentity extends SingleSymphonyIdentity implements SymphonyIdentity {
	
    // PKCS#8 format
    static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
    static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

    // PKCS#1 format
    static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
    
    static final String PEM_CERT_START = "-----BEGIN CERTIFICATE-----";
    static final String PEM_CERT_END = "-----END CERTIFICATE-----";

    static final String PEM_PUBLIC_START = "-----BEGIN PUBLIC KEY-----";
    static final String PEM_PUBLIC_END =  "-----END PUBLIC KEY-----";


	
	public PemSymphonyIdentity() {
		super();
	}

	public PemSymphonyIdentity(RSAPrivateCrtKey privateKey, String email, X509Certificate[] chain) {
		super(privateKey, email, chain, getCommonName(chain));
	}

	public PemSymphonyIdentity(String privateKeyPem, String commonName, String email) {
		super(createPrivateKeyFromString(privateKeyPem), email, null, commonName);
	}

	public PemSymphonyIdentity(String privateKeyPem, String[] certificatePems, String email) {
		this(createPrivateKeyFromString(privateKeyPem), email, 
				Arrays.stream(certificatePems).map(pem -> createCertificate(pem)).toArray(X509Certificate[]::new));
	}
	
	public static RSAPublicKey createPublicKeyFromString(String key) {
		try {
			String publicKeyPEM = removeFurniture(key);
			byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpec);
			return pubKey;
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't create public key from " + key.substring(0, 6)+"...", e);
		}
	}
	
	public static RSAPrivateCrtKey createPrivateKeyFromString(String privateKeyPem) {
        if (privateKeyPem.indexOf(PEM_PRIVATE_START) != -1) { // PKCS#8 format
            privateKeyPem = removeFurniture(privateKeyPem);
    		byte[] bytes = Base64.getDecoder().decode(privateKeyPem);
            return createPrivateKeyFromPKCS8(bytes);
        } else if (privateKeyPem.indexOf(PEM_RSA_PRIVATE_START) != -1) {  // PKCS#1 format
            privateKeyPem = removeFurniture(privateKeyPem);
    		byte[] bytes = Base64.getDecoder().decode(privateKeyPem);
            return createPrivateKeyFromPKCS1(bytes);
        } else {
            throw new IdentityConfigurationException("Not supported format of a private key", null);
        }
    }

	protected static String removeFurniture(String in) {
		in = in.replace(PEM_PRIVATE_START, "").replace(PEM_PRIVATE_END, "");
		in = in.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
		in = in.replace(PEM_PUBLIC_START, "").replace(PEM_PUBLIC_END, "");
		in = in.replace(PEM_CERT_START, "").replace(PEM_CERT_END, "");
		in = in.replaceAll("\n", "");
		in = in.replaceAll("\\s", "");
		return in;
	}

	private static RSAPrivateCrtKey createPrivateKeyFromPKCS1(byte[] pkcs1Bytes) {
	    int pkcs1Length = pkcs1Bytes.length;

		   int totalLength = pkcs1Length + 22;
		    byte[] pkcs8Header = new byte[] {
		            0x30, (byte) 0x82, (byte) ((totalLength >> 8) & 0xff), (byte) (totalLength & 0xff), // Sequence + total length
		            0x2, 0x1, 0x0, // Integer (0)
		            0x30, 0xD, 0x6, 0x9, 0x2A, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xF7, 0xD, 0x1, 0x1, 0x1, 0x5, 0x0, // Sequence: 1.2.840.113549.1.1.1, NULL
		            0x4, (byte) 0x82, (byte) ((pkcs1Length >> 8) & 0xff), (byte) (pkcs1Length & 0xff) // Octet string + length
		    };
		    byte[] pkcs8bytes = join(pkcs8Header, pkcs1Bytes);
		    return createPrivateKeyFromPKCS8(pkcs8bytes);
		}

	private static byte[] join(byte[] byteArray1, byte[] byteArray2){
	    byte[] bytes = new byte[byteArray1.length + byteArray2.length];
	    System.arraycopy(byteArray1, 0, bytes, 0, byteArray1.length);
	    System.arraycopy(byteArray2, 0, bytes, byteArray1.length, byteArray2.length);
	    return bytes;
	}
		
	protected static RSAPrivateCrtKey createPrivateKeyFromPKCS8(byte[] pkcs8Bytes) {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return (RSAPrivateCrtKey) factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8Bytes));
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't create private key", e);
		}
	}
	
	
	public static X509Certificate createCertificate(String pem) {
		try {
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			pem = removeFurniture(pem);
			pem = PEM_CERT_START + "\n" + pem + "\n" + PEM_CERT_END;
			X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(pem.getBytes()));
			return cer;
		} catch (CertificateException e) {
			throw new IdentityConfigurationException("Couldn't create certificate from " + pem, e);
		}
	}

}
