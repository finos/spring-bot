package com.symphony.spring.app.jwt;

import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.AUD;
import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.EXP;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements JWT verification using {@link SignatureVerifierProvider}s.  Note this doesn't do any actual token storage, 
 * and doesn't implement anything to do with refresh tokens.   You may subclass if you need to add this functionality.
 * 
 * @author Rob Moffat
 *
 */
public class SymphonyJwtTokenStore implements TokenStore {

	protected List<SignatureVerifierProvider> signatureVerifierProviders;
	protected JwtClaimsSetVerifier claimsSetVerifier;
	protected ObjectMapper objectMapper;
	protected String principalField = "emailAddress";

	public SymphonyJwtTokenStore(List<SignatureVerifierProvider> signatureVerifierProviders, ObjectMapper objectMapper) {
		super();
		this.signatureVerifierProviders = signatureVerifierProviders;
		this.objectMapper = objectMapper;
	}
	
	protected Map<String, Object> decode(String token) {
		Jwt jwt;
		Map<String, Object> claims;
		try {
			jwt = JwtHelper.decode(token);

			String claimsStr = jwt.getClaims();
			claims = objectMapper.readValue(claimsStr, new TypeReference<Map<String, Object>>() {});
			if (claims.containsKey(EXP) && claims.get(EXP) instanceof Integer) {
				Integer intValue = (Integer) claims.get(EXP);
				claims.put(EXP, new Long(intValue));
			}
		} catch (Exception e) {
			throw new InvalidTokenException("Couldn't parse JWT Token: ", e);
		}
		
		verifyJwt(jwt, claims);

		if (this.claimsSetVerifier != null) {
			this.claimsSetVerifier.verify(claims);
		}
		return claims;

	}

	private void verifyJwt(Jwt jwt, Map<String, Object> claims) {
		for (SignatureVerifierProvider signatureVerifierProvider : signatureVerifierProviders) {
			SignatureVerifier sv = signatureVerifierProvider.getSignatureVerifier(claims);
			
			if (sv != null) {
				try {
					jwt.verifySignature(sv);
				} catch (Exception e) {
					throw new InvalidTokenException("Couldn't verify signature: ", e);
				}
				return;
			}
		}
		
		throw new InvalidTokenException("Could not find a signature verifier for "+claims);
	}

	@Override
	@SuppressWarnings("unchecked")
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		Map<String, String> parameters = ((Map<String, Object>) token.getAdditionalInformation().get("user")).entrySet()
				.stream().collect(Collectors.toMap(e -> e.getKey(), v -> ""+v.getValue()));
		Set<String> scope = token.getScope();
		Authentication user = getPrincipal(token);
		String clientId = (String) parameters.get("sub");
		OAuth2Request request = new OAuth2Request(parameters, clientId, Collections.emptyList(), true, scope, null, null, null, null);
		return new OAuth2Authentication(request, user);	
	}
	
	@SuppressWarnings("unchecked")
	protected Authentication getPrincipal(OAuth2AccessToken token) {
		Map<String, Object> details = (Map<String, Object>) token.getAdditionalInformation().get("user");
		String principal = (String) details.get(principalField);
		if (principal == null) {
			throw new InvalidTokenException("Couldn't find field "+principalField+" in token user section: "+details);
		}
		
		return new UsernamePasswordAuthenticationToken(principal,"Bearer", Collections.emptyList());
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		return readAuthentication(readAccessToken(token));
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		Map<String, Object> details = decode(tokenValue);
		Map<String, Object> info = new HashMap<String, Object>(details);
		info.remove(EXP);
		info.remove(AUD);
		DefaultOAuth2AccessToken out = new DefaultOAuth2AccessToken(tokenValue);
		out.setAdditionalInformation(info);
		out.setExpiration(new Date((Long) details.get(EXP) * 1000L));
		out.setScope(Collections.singleton((String) details.get(AUD)));
		return out;
	}
	
	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
	}

	private <X> X throwNotImplemented() {
		throw new UnsupportedOperationException("Not implemented for SymphonyJwtTokenStore");
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		throwNotImplemented();
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		return throwNotImplemented();
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return throwNotImplemented();
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		throwNotImplemented();
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		throwNotImplemented();
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		return throwNotImplemented();
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		return throwNotImplemented();
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		return throwNotImplemented();
	}
	

	public JwtClaimsSetVerifier getClaimsSetVerifier() {
		return claimsSetVerifier;
	}

	public void setClaimsSetVerifier(JwtClaimsSetVerifier claimsSetVerifier) {
		this.claimsSetVerifier = claimsSetVerifier;
	}

	public String getPrincipalField() {
		return principalField;
	}

	public void setPrincipalField(String principalField) {
		this.principalField = principalField;
	}

}
