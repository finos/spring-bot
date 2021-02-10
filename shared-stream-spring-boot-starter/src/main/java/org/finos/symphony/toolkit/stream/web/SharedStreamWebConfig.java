package org.finos.symphony.toolkit.stream.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.cluster.transport.HttpMulticaster;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * This initializes shared stream http communication, but only if the user is using spring-web.
 * This test is done by checking McvConfiguration exists.
 * 
 * @author moffrob
 *
 */
@ConditionalOnWebApplication
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@AutoConfigureBefore({SharedStreamSingleBotConfig.class})
@EnableConfigurationProperties(SharedStreamProperties.class)
public class SharedStreamWebConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamWebConfig.class);
	
	@Autowired
	SharedStreamProperties streamProperties;

	/**
	 * This will be the regular object mapper defined for REST JSON conversion by 
	 * spring-web.
	 */
	@Autowired
	ObjectMapper objectMapper;
	
	@Value("${server.port:8080}")
	private String serverPort;

	public static class SymphonyStreamUrlMapping extends SimpleUrlHandlerMapping {}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyStreamUrlMapping symphonyStreamUrlMapping(HttpClusterMessageController clusterMessageController) {
		SymphonyStreamUrlMapping out = new SymphonyStreamUrlMapping();
		Map<String, Object> sm = Collections.singletonMap(streamProperties.getEndpointPath(), clusterMessageController);
		out.setUrlMap(sm);
		return out;
	}
	
	private View symphonyJsonOutputView() {
		MappingJackson2JsonView out = new MappingJackson2JsonView(objectMapper);
		out.setPrettyPrint(true);
		out.setExtractValueFromSingleKeyModel(true);
		return out;
	}

	protected String hostNameAndPort() {
		try {
			return InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;
		} catch (UnknownHostException e) {
			throw new UnsupportedOperationException("Couldn't determine local host address", e);
		}
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Multicaster multicaster() {
		return new HttpMulticaster(selfParticipant());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Participant selfParticipant() {
		String endpointPath = streamProperties.getEndpointPath();
		endpointPath = endpointPath.startsWith("/") ? endpointPath : "/" + endpointPath;
		String hostAndPort = StringUtils.isEmpty(streamProperties.getEndpointHostAndPort()) ? hostNameAndPort() : streamProperties.getEndpointHostAndPort();
		String scheme = streamProperties.getEndpointScheme().toString().toLowerCase();
		String url = scheme+"://" + hostAndPort + endpointPath;
		LOG.info("Cluster starting up. This participant id: "+url);
		return new Participant(url);	
	}
	
	@Bean
	@ConditionalOnMissingBean
	public HttpClusterMessageController httpClusterMessageController() {
		return new HttpClusterMessageController(symphonyJsonOutputView(), objectMapper);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public HealthSupplier healthSupplier(HealthEndpoint health) {
		return () -> health.health().getStatus() == Status.UP;
	}
}
