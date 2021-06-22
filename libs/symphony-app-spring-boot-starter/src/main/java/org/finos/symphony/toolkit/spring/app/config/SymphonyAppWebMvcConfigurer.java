package org.finos.symphony.toolkit.spring.app.config;

import java.util.List;

import org.finos.symphony.toolkit.spring.app.SymphonyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SymphonyAppWebMvcConfigurer implements WebMvcConfigurer {

	public static final String[] ALLOWED_METHODS = { "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH" };


	@Autowired
	List<SymphonyController> controllers;
	
	/**
	 * Enables Symphony to do cross-origin request to load the bundle.
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods(ALLOWED_METHODS);
	}

	/**
	 * See https://stackoverflow.com/questions/40418441/spring-security-cors-filter
	 * This just updates cors for SymphonyController objects, allowing Symphony to load the controller javascript, etc.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    final CorsConfiguration configuration = new CorsConfiguration();
	    configuration.applyPermitDefaultValues();
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    controllers.stream().map(c -> c.getPath()).forEach(p -> source.registerCorsConfiguration("/**", configuration));
	    return source;
	}

}
