package com.github.deutschebank.symphony.spring.api.builders;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.cxf.CXFApiBuilder;

/**
 * Supplies clients if you are using Apache CXF.
 * 
 * @author Rob Moffat
 *
 */
@ConditionalOnClass({WebClient.class})
@Configuration
public class CXFApiBuilderConfig {

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
				 return new CXFApiBuilder();
			}
		};
	}
	
}
