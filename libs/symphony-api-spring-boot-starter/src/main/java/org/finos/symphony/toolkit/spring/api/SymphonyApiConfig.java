package org.finos.symphony.toolkit.spring.api;

import java.io.IOException;

import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.id.IdentityConfigurationException;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@EnableConfigurationProperties(SymphonyApiProperties.class)
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
public class SymphonyApiConfig {

	private static final Logger LOG = LoggerFactory.getLogger(SymphonyApiConfig.class); 

	public static final String SINGLE_BOT_IDENTITY_PROPERTY = "symphony.bot.identity";
	
	public static final String SINGLE_BOT_IDENTITY_BEAN = "botIdentity";

	public static final String SINGLE_BOT_INSTANCE_BEAN = "singleBotInstance";

	public static final String API_INSTANCE_FACTORY = "apiInstanceFactory";

	@Autowired
	SymphonyApiProperties symphonyProperties;
	
	@Autowired
	ApiBuilderFactory apiBuilderFactory;

	@Value("${symphony.bot.pod-to-use:}")
	String podId;
		
	@Autowired
	ResourceLoader resourceLoader; 
	
	@Autowired
	ObjectMapper mapper;
		
	@ConfigurationProperties(prefix = SINGLE_BOT_IDENTITY_PROPERTY)
	@Bean
	IdentityProperties identityDetails() {
		return new IdentityProperties();
	}
	
	@Bean(name=SINGLE_BOT_IDENTITY_BEAN)
	@ConditionalOnMissingBean
	@ConditionalOnExpression("'${"+SINGLE_BOT_IDENTITY_PROPERTY+".email:}${"+SINGLE_BOT_IDENTITY_PROPERTY+".location:}' != ''" )
	public SymphonyIdentity botIdentity() throws IOException {
		LOG.warn("Loading identity from "+SINGLE_BOT_IDENTITY_PROPERTY);
		SymphonyIdentity id = IdentityProperties.instantiateIdentityFromDetails(resourceLoader, identityDetails(), mapper);
		
		if (id == null) {
			throw new IdentityConfigurationException("Couldn't create bot identity from properties", null);
		}
	
		return id;
	}	

	@Bean(name=SINGLE_BOT_IDENTITY_BEAN)
	@ConditionalOnMissingBean
	@ConditionalOnExpression("#{T(com.symphony.api.id.testing.TestIdentityProvider).hasTestIdentity()}")
	public SymphonyIdentity testBotIdentity() throws IOException {
		LOG.warn("No identity defined in spring configuration.  Resorting to test identity");
		return TestIdentityProvider.getTestIdentity();
	}	
	
	@Bean(name=API_INSTANCE_FACTORY)
	@ConditionalOnMissingBean
	public ApiInstanceFactory apiInstanceFactory(@Autowired ApiBuilderFactory abf, @Autowired(required=false) HealthContributorRegistry hir, @Autowired(required=false) MeterRegistry mr) {
		return new DefaultApiInstanceFactory(abf, hir, mr, mapper);
	}
	
	@Bean(name=SINGLE_BOT_INSTANCE_BEAN)
	@ConditionalOnMissingBean
	@ConditionalOnBean(name = SINGLE_BOT_IDENTITY_BEAN)
	public ApiInstance singleBotApiInstance(ApiInstanceFactory bif, 
			@Qualifier(SINGLE_BOT_IDENTITY_BEAN) SymphonyIdentity id,  
			@Autowired(required=false) @Qualifier(SymphonyApiTrustManagersConfig.SYMPHONY_TRUST_MANAGERS_BEAN) TrustManagerFactory trustManagerFactory) 
				throws Exception {
		PodProperties pp = getMainPodProperties();
		return bif.createApiInstance(id, pp, trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers());
	}

	private PodProperties getMainPodProperties() {	
		if (!StringUtils.isEmpty(podId)) {
			for (PodProperties pp : symphonyProperties.getApis()) {
				if (podId.equals(pp.getId())) {
					return pp;
				}
			}
		} 
		
		if (!CollectionUtils.isEmpty(symphonyProperties.getApis())) {
			return symphonyProperties.getApis().get(0);
		}
		
		throw new BeanInstantiationException(ApiBuilder.class, "No pod configured at symphony.apis");
	}

}
