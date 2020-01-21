package com.github.deutschebank.symphony.spring.app.id;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Tools for using BouncyCastle to generate certificates.
 * 
 * @author Rob Moffat
 *
 */
public class BCCertificateTools implements CertificateTools {
	
    Provider bcProvider = new BouncyCastleProvider();
    
	public BCCertificateTools() {
		super();
		Security.addProvider(bcProvider);
	}

	@Override
	public X509Certificate createSelfSignedCertificate(String appId, KeyPair keyPair)
			throws Exception {
	    long now = System.currentTimeMillis();
		Date startDate = new Date(now);

	    X500Name dnName = new X500Name("CN="+appId);
	    BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number
	    Date endDate = getCertificateExpiry(startDate);
	    String signatureAlgorithm = "SHA256WithRSA"; // <-- Use appropriate signature algorithm based on your keyPair algorithm.
	    ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());
	    JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());
	    return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
	}

	protected Date getCertificateExpiry(Date startDate) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(startDate);
	    calendar.add(Calendar.YEAR, 10); // <-- 10 Yr validity.  Otherwise apps expire, major faff.
	    Date endDate = calendar.getTime();
		return endDate;
	}

	@Override
	public KeyPair createKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(4096);
		KeyPair keyPair = keyGen.generateKeyPair();
		return keyPair;
	}
}
