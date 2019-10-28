package com.github.deutschebank.symphony.spring.app.jwt;

import java.util.Map;

import org.springframework.security.jwt.crypto.sign.SignatureVerifier;

public interface SignatureVerifierProvider {

	public SignatureVerifier getSignatureVerifier(Map<String, Object> claims);
}

