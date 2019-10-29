package com.github.deutschebank.symphony.spring.app.tokens.pod;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;

import com.github.deutschebank.symphony.spring.api.builders.ApiBuilderFactory;
import com.github.deutschebank.symphony.spring.app.jwt.SignatureVerifierProvider;
import com.symphony.api.id.PemSymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.ExtensionAppTokens;
import com.symphony.api.model.PodCertificate;
import com.symphony.api.pod.PodApi;

public abstract class AbstractPodTokenStrategy<X> implements PodTokenStrategy, SignatureVerifierProvider {

	public static final Logger LOG = LoggerFactory.getLogger(PodTokenStrategy.class);

	
	protected SymphonyIdentity appIdentity;
	protected ApiBuilderFactory abf;
	protected TrustManager[] trustManagers;
	
	public AbstractPodTokenStrategy(SymphonyIdentity appIdentity,
			ApiBuilderFactory abf, TrustManager[] trustManagers) {
		this.appIdentity = appIdentity;
		this.abf = abf;
		this.trustManagers = trustManagers;
	}

	public ExtensionAppTokens getTokens(String appToken, String podId) throws Exception {
		X pod = getPodProperties(podId);
		
		if (pod == null) {
			return null;
		}
		
		if (hasCerts()) {
			return certBasedRequest(appToken, pod);
		} else {
			return pubKeyBasedRequest(appToken, pod);
		}
	}
	
	protected abstract X getPodProperties(String podId);

	protected ExtensionAppTokens pubKeyBasedRequest(String appToken, X pod) {
		throw new UnsupportedOperationException("PubKey app circle of trust not implemented yet");
	}


	protected abstract ExtensionAppTokens certBasedRequest(String appToken, X pod) throws Exception;


	private boolean hasCerts() {
		return (appIdentity.getCertificateChain()!=null) && (appIdentity.getCertificateChain().length > 0);
	}

	@Override
	public SignatureVerifier getSignatureVerifier(Map<String, Object> claims) {
		PodApi podApi;
		try {
			podApi = getPodApi(claims);
		} catch (Exception e) {
			LOG.warn("Problem setting up podApi for "+claims,e);
			return null;
		}
		
		if (podApi == null) {
			return null;
		}
		
		PodCertificate certCall = podApi.v1PodcertGet();
		String pemCert = certCall.getCertificate();
		X509Certificate cert  = PemSymphonyIdentity.createCertificate(pemCert);	
		RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
		return new RsaVerifier(publicKey, "SHA512withRSA");
	}

	protected abstract PodApi getPodApi(Map<String, Object> claims) throws Exception;

	protected String getCompanyId(Map<String, Object> claims) {
		if (claims.containsKey("user")) {
			return ""+((Map<String, Object>) claims.get("user")).get("companyId");
		} else {
			return "-none-";
		}
 	}

	
}