package org.finos.symphony.toolkit.koreai.spring;

import java.io.IOException;

import javax.inject.Named;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.spring.api.ApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;

@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class, SharedStreamWebConfig.class})
@EnableConfigurationProperties({KoreAIProperties.class, SymphonyStreamProperties.class, SymphonyApiProperties.class})
public class KoreAIConfig implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(KoreAIConfig.class);
	
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
	public SymphonyStreamHandler createHandler(KoreAIInstanceProperties prop, ApiInstance instance) {
		return koreAIBridgeFactory().buildBridge(prop, instance);	
	}
	
	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ApiInstance symphonyAPIInstance(KoreAIInstanceProperties props) {
		try {
			SymphonyIdentity symphonyBotIdentity = IdentityProperties.instantiateIdentityFromDetails(rl, props.getSymphonyBot(), koreAIObjectMapper());
			TrustManager[] tms = tmf == null ? null: tmf.getTrustManagers();
			ApiInstance apiInstance = symphonyAPIInstanceFactory.createApiInstance(symphonyBotIdentity, firstPodProperties(), tms);
			LOG.info("Constructed API Factory for {} ",props.getName());
			return apiInstance;
		} catch (Exception e) {
			LOG.error("Couldn't create API instance for {} ",props.getName());
			throw new UnsupportedOperationException("Couldn't get api instance: ", e);
		}
	}
	
	@Bean
	@ConditionalOnMissingBean
	public KoreAIBridgeFactory koreAIBridgeFactory() {
		return new KoreAIBridgeFactoryImpl(
			rl, 
			koreAIObjectMapper(), 
			koreProperties, 
			streamProperties, 
			ec, 
			mc, 
			participant,
			symphonySharedLog());
	}
	
	/**
	 * This constructs a SharedLog using just the first bot to decide the cluster leadership
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "symphony.stream.coordination-stream-id")
	public SymphonyRoomSharedLog symphonySharedLog() {
		ApiInstance symphonyInstance = getFirstSymphonyAPIInstance();
		
		return new SymphonyRoomSharedLog(
				streamProperties.getCoordinationStreamId(), 
				symphonyInstance.getAgentApi(MessagesApi.class), 
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
	}

	protected ApiInstance getFirstSymphonyAPIInstance() {
		if ((koreProperties.getInstances() == null) || (koreProperties.getInstances().isEmpty())) {
			throw new IllegalArgumentException("No KoreAI Bridge instances defined in symphony.koreai.instances");
		} 
		
		if (StringUtils.isEmpty(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  Please set symphony.stream.coordination-stream-id");
		}
				
		KoreAIInstanceProperties firstBot = koreProperties.getInstances().get(0);
		ApiInstance symphonyInstance = ctx.getBean(ApiInstance.class, firstBot);
		return symphonyInstance;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for (KoreAIInstanceProperties instance : koreProperties.getInstances()) {
				ctx.getBean(SymphonyStreamHandler.class, instance, symphonyAPIInstance(instance));
		}
	}
	


	/**
	 * First pod is the only one used for bridging KoreAI.  In future, make this configurable.
	 */
	protected PodProperties firstPodProperties() {
		PodProperties pp;
		if (apiProperties.getApis().size() != 1) {
			throw new IllegalArgumentException("KoreAI Bridge must have the details of a single pod configured");
		}
		
		pp = apiProperties.getApis().get(0);
		return pp;
	}
	
	/**
	 * This provides the first symphony bot, used to manage the cluster
	 */
	protected SymphonyIdentity firstBotIdentity() throws IOException {
		ApiInstance api = getFirstSymphonyAPIInstance();
		
		if (api != null) {
			return api.getIdentity();
		} else {
			LOG.warn("No identity defined in spring configuration.  Resorting to test identity");
			return TestIdentityProvider.getTestIdentity();
		}
	}	
	
	
}
