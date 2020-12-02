package org.finos.symphony.toolkit.spring.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.spring.api.properties.TrustStoreProperties;
import org.finos.symphony.toolkit.spring.api.properties.TrustStoreProperties.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicatorRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.id.PemSymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;
import com.symphony.api.pod.SystemApi;

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

	public static final String SYMPHONY_TRUST_MANAGERS_BEAN = "symphonyTrustManagers";

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
	@Lazy
	@ConditionalOnMissingBean
	public SymphonyIdentity botIdentity() throws IOException {
		SymphonyIdentity id = IdentityProperties.instantiateIdentityFromDetails(resourceLoader, identityDetails(), mapper);
		
		if (id != null) {
			return id;
		} else {
			LOG.warn("No identity defined in spring configuration.  Resorting to test identity");
			return TestIdentityProvider.getTestIdentity();
		}
	}	

	@Bean
	public static ApiBeanRegistration podApiRegistration() {
		return new ApiBeanRegistration(SINGLE_BOT_INSTANCE_BEAN, SystemApi.class.getPackage().getName(), "getPodApi");
	}
	
	@Bean
	public static ApiBeanRegistration agentApiRegistration() {
		return new ApiBeanRegistration(SINGLE_BOT_INSTANCE_BEAN, MessagesApi.class.getPackage().getName(), "getAgentApi");
	}
	
	@Bean(name=SYMPHONY_TRUST_MANAGERS_BEAN)
	@ConditionalOnMissingBean(name=SYMPHONY_TRUST_MANAGERS_BEAN)
	public TrustManagerFactory symphonyTrustManagerFactory() throws Exception {
		if (symphonyProperties.getTrustStore() != null) {
			TrustStoreProperties tsp = symphonyProperties.getTrustStore();
			InputStream is = resourceLoader.getResource(tsp.getLocation()).getInputStream();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			String storeType = tsp.getType() == Type.PEMS ? "PKCS12" : tsp.getType().name();
			KeyStore keystore = KeyStore.getInstance(storeType);
			
			if (tsp.getType() == Type.PEMS) {
				keystore.load(null, null);
				Scanner scanner = new Scanner(is);
				scanner.useDelimiter("-----END CERTIFICATE-----");
				int id = 0;
				while (scanner.hasNext()) {
					String certstring = scanner.next();
					if (certstring.length()>5) {
						X509Certificate cert = PemSymphonyIdentity.createCertificate(certstring);
						keystore.setCertificateEntry("cert-"+(id++), cert);
					}				
				}
				scanner.close();
			} else {
				keystore.load(is, tsp.getPassword().toCharArray());
			}

			tmf.init(keystore);
			return tmf;
			
		} else {
			return null;
		}
	}
	
	@Bean(name=API_INSTANCE_FACTORY)
	@ConditionalOnMissingBean
	public ApiInstanceFactory apiInstanceFactory(@Autowired ApiBuilderFactory abf, @Autowired(required=false) HealthIndicatorRegistry hir, @Autowired(required=false) MeterRegistry mr) {
		return new DefaultApiInstanceFactory(abf, hir, mr, mapper);
	}
	
	@Bean(name=SINGLE_BOT_INSTANCE_BEAN)
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "privateKey", prefix = SINGLE_BOT_IDENTITY_PROPERTY)
	@Lazy
	public ApiInstance singleBotApiInstance(ApiInstanceFactory bif, @Qualifier(SINGLE_BOT_IDENTITY_BEAN) SymphonyIdentity id,  @Autowired(required=false) @Qualifier(SYMPHONY_TRUST_MANAGERS_BEAN) TrustManagerFactory trustManagerFactory) throws Exception {
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
