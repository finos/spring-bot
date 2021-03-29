package org.finos.symphony.toolkit.spring.app.id;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public interface CertificateTools {

	X509Certificate createSelfSignedCertificate(String appId, KeyPair keyPair) throws Exception;

	KeyPair createKeyPair() throws NoSuchAlgorithmException;

}