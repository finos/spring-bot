package com.github.deutschebank.symphony.spring.app.jwt;

import java.util.Map;

import org.springframework.security.jwt.crypto.sign.SignatureVerifier;

/**
 * Interface for verifying signatures on Symphony-provided JWT tokens.
 * 
 * @author robmoffat
 *
 */
public interface SignatureVerifierProvider {

	public SignatureVerifier getSignatureVerifier(Map<String, Object> claims);
}

