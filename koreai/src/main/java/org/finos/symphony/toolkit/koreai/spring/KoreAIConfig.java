package org.finos.symphony.toolkit.koreai.spring;

import javax.inject.Named;
import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.spring.api.ApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.spring.SharedStreamConfig.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.spring.SharedStreamWebConfig;
import org.finos.symphony.toolkit.stream.spring.SymphonyStreamProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class, SharedStreamWebConfig.class})
@EnableConfigurationProperties({KoreAIProperties.class, SymphonyStreamProperties.class, SymphonyApiProperties.class})
public class KoreAIConfig implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(KoreAIConfig.class);

	public static final String KORE_AI_BRIDGE_FACTORY_BEAN = "koreAiBridgeFactory";
	
	@Autowired
	KoreAIProperties koreProperties;
	
	@Autowired
	SymphonyStreamProperties streamProperties;
	
	@Autowired
	SymphonyApiProperties apiProperties;
	
	@Autowired
	ResourceLoader rl;
	
	@Autowired 
	ApiInstanceFactory symphonyAPIInstanceFactory;
	
	@Autowired
	ApplicationContext ctx;
	
	@Autowired
	ExceptionConsumer ec;

	@Autowired
	Multicaster mc;

	@Autowired
	Participant participant;

	@Autowired(required = false)
	@Named(SymphonyApiConfig.SYMPHONY_TRUST_MANAGERS_BEAN)
	TrustManagerFactory tmf;
		
	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper symphonyObjectMapper() {
		return koreAIObjectMapper();
	}

	public static ObjectMapper koreAIObjectMapper() {
		ObjectMapper out = new ObjectMapper();
		ObjectMapperFactory.initialize(out, ObjectMapperFactory
			.extendedSymphonyVersionSpace(
				LogMessage.VERSION_SPACE,	
				new VersionSpace(KoreAIResponse.class.getPackage().getName(), "1.0"),
				new VersionSpace(ObjectNode.class.getPackage().getName(), "1.0")));
		return out;
	}
		
	
	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SymphonyStreamHandler createHandler(KoreAIInstanceProperties props) {
		return koreAIBridgeFactory().buildBridge(props);	
	}
	
	
	@Bean(name = KORE_AI_BRIDGE_FACTORY_BEAN)
	@ConditionalOnMissingBean
	public KoreAIBridgeFactory koreAIBridgeFactory() {
		PodProperties pp;
		if (apiProperties.getApis().size() != 1) {
			throw new IllegalArgumentException("KoreAI Bridge must have the details of a single pod configured");
		}
		
		pp = apiProperties.getApis().get(0);
		
		return new KoreAIBridgeFactoryImpl(
			rl, 
			koreAIObjectMapper(), 
			tmf,
			pp, 
			koreProperties, 
			streamProperties, 
			symphonyAPIInstanceFactory, 
			ec, 
			mc, 
			participant);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (koreProperties.getInstances() == null) {
			LOG.error("No KoreAI Bridge instances defined in symphony.koreai.instances");
		} else {
			for (KoreAIInstanceProperties instance : koreProperties.getInstances()) {
				ctx.getBean(SymphonyStreamHandler.class, instance);
			}
		}
	}
	
}
