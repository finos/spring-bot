package org.finos.symphony.toolkit.spring.app.config;

import java.util.List;

import org.finos.symphony.toolkit.spring.app.SymphonyAppConfig;
import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.finos.symphony.toolkit.spring.app.SymphonyController;
import org.finos.symphony.toolkit.spring.app.jwt.SignatureVerifierProvider;
import org.finos.symphony.toolkit.spring.app.jwt.SymphonyJwtTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.SymphonyIdentity;

@SuppressWarnings("deprecation")
@EnableResourceServer
@Configuration
public class SymphonyAppResourceServerConfigurer implements ResourceServerConfigurer {

	@Qualifier(SymphonyAppConfig.APP_IDENTITY_BEAN)
	@Autowired
	private SymphonyIdentity appIdentity;
	
	@Autowired
	private SymphonyAppProperties appProperties;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private List<SignatureVerifierProvider> signatureVerifierProviders;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		if (appProperties.isJwt()) {
			resources.tokenStore(new SymphonyJwtTokenStore(signatureVerifierProviders, objectMapper));
			resources.resourceId(appIdentity.getCommonName());
		}
	}
	

	@Autowired
	List<SymphonyController> controllers;

	/**
	 * Makes sure all the symphony-app endpoints are unsecured.
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		String[] pages = controllers.stream().map(c -> c.getPath()).toArray(String[]::new); 
		http.cors();
		http.headers().xssProtection().disable();
		http.headers().frameOptions().disable();
		http.authorizeRequests()
			.antMatchers(pages).permitAll();
	}

	
}
