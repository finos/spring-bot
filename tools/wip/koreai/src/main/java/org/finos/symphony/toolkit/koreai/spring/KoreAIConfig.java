package org.finos.symphony.toolkit.koreai.spring;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.net.ssl.TrustManagerFactory;

import org.finos.springbot.entities.VersionSpaceHelp;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.symphony.stream.handler.ExceptionConsumer;
import org.finos.springbot.symphony.stream.handler.SymphonyStreamHandler;
import org.finos.springbot.symphony.stream.handler.SymphonyStreamHandlerFactory;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.spring.api.SymphonyApiTrustManagersConfig;
import org.finos.symphony.toolkit.spring.api.factories.ApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.symphony.api.model.V4User;

@Configuration
@EnableConfigurationProperties({KoreAIProperties.class, SymphonyApiProperties.class})
@EnableWebMvc
@Import(DataHandlerConfig.class)
public class KoreAIConfig implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(KoreAIConfig.class);
	public static final String KORE_AI_BRIDGE_FACTORY_BEAN = "KoreAiBridgeFactoryBean";
	public static final String KORE_AI_BRIDGE_LIST_BEAN = "KoreAiBridgeListBean";
	
	@Autowired
	KoreAIProperties koreProperties;
	
	@Autowired
	SymphonyApiProperties apiProperties;
	
	@Autowired
	ResourceLoader rl;
	
	@Autowired 
	ApiInstanceFactory symphonyAPIInstanceFactory;
	
	@Autowired
	SymphonyStreamHandlerFactory sshf;

	@Autowired(required = false)
	@Named(SymphonyApiTrustManagersConfig.SYMPHONY_TRUST_MANAGERS_BEAN)
	TrustManagerFactory tmf;
	
	@Autowired
	EntityJsonConverter ejc;
		
	@Bean
	@ConditionalOnMissingBean
	public ExceptionConsumer exceptionConsumer() {
		return (e) -> LOG.error("StreamException: ", e);
	}
	
	@Bean(name=KORE_AI_BRIDGE_FACTORY_BEAN)
	@ConditionalOnMissingBean
	public KoreAIBridgeFactory koreAIBridgeFactory() {
		return new KoreAIBridgeFactoryImpl(
			rl, 
			ejc, 
			koreProperties, 
			sshf, 
			symphonyAPIInstanceFactory, 
			tmf, 
			apiProperties);
	}

	/**
	 * For every entry in the symphony.koreai.instances space, this will create a {@link SymphonyStreamHandler} bean
	 * definition.
	 */
	@Bean(name = KORE_AI_BRIDGE_LIST_BEAN)
	@ConditionalOnMissingBean
	public List<SymphonyStreamHandler> bridgeRegistrations() {
		List<SymphonyStreamHandler> out = koreProperties.getInstances().stream()
			.map(i -> koreAIBridgeFactory().buildBridge(i))
			.filter(e -> e!=null)
			.collect(Collectors.toList());
		
		LOG.info("Constructed {} bridges", out.size());
		out.forEach(c -> LOG.debug(c.getInstance().getIdentity().getEmail()));
		return out;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		addVersionSpaces(ejc);
	}

	public static void addVersionSpaces(EntityJsonConverter ejc) {
		ejc.addVersionSpace(new VersionSpace(KoreAIResponse.class));
		ejc.addVersionSpace(new VersionSpace(ObjectNode.class));
		ejc.addVersionSpace(new VersionSpace(V4User.class));
		VersionSpaceHelp.basicSymphonyVersionSpace()
			.stream().forEach(vs -> ejc.addVersionSpace(vs));
	}
	
}
