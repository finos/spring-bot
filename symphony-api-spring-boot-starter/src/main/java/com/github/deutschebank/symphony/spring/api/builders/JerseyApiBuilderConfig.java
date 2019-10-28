package com.github.deutschebank.symphony.spring.api.builders;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.jersey.JerseyApiBuilder;

/**
 * If you are using Jersey, this class will supply a builder.
 * 
 * @author Rob Moffat
 *
 */
@ConditionalOnClass({JerseyClientBuilder.class})
@Configuration
public class JerseyApiBuilderConfig {

	@Bean
	@Lazy
	@ConditionalOnMissingBean
	public ApiBuilderFactory apiBuilderFactory() {
		return new ApiBuilderFactory() {
			
			@Override
			public Class<?> getObjectType() {
				return ConfigurableApiBuilder.class;
			}
			
			@Override
			public ConfigurableApiBuilder getObject() throws Exception {
				 return new JerseyApiBuilder();
			}
		};
	}
	
}
