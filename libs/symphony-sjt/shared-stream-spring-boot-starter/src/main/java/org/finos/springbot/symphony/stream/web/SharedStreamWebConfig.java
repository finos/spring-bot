package org.finos.springbot.symphony.stream.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.symphony.stream.SharedStreamProperties;
import org.finos.springbot.symphony.stream.cluster.HealthSupplier;
import org.finos.springbot.symphony.stream.cluster.Multicaster;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
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
@AutoConfigureAfter({SymphonyApiConfig.class, WebMvcAutoConfiguration.EnableWebMvcConfiguration.class})
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
		Participant me = selfParticipant();
		Multicaster out = new HttpMulticaster(me, (int) streamProperties.getTimeoutMs() / 2);
		return out;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Participant selfParticipant() {
		String endpointPath = streamProperties.getEndpointPath();
		endpointPath = endpointPath.startsWith("/") ? endpointPath : "/" + endpointPath;
		String hostAndPort = StringUtils.hasText(streamProperties.getEndpointHostAndPort()) ?  streamProperties.getEndpointHostAndPort() : hostNameAndPort();
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
	public SymphonyStreamUrlMapping symphonyStreamUrlMapping(HttpClusterMessageController controller) {
		SymphonyStreamUrlMapping out = new SymphonyStreamUrlMapping();
		Map<String, Object> sm = Collections.singletonMap(streamProperties.getEndpointPath(), controller);
		out.setUrlMap(sm);
		out.setOrder(1);
		LOG.info("Mapped {} to clusterMessageController", streamProperties.getEndpointPath());
		return out;
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public HealthSupplier healthSupplier(HealthEndpoint health) {
		return () -> health.health().getStatus() == Status.UP;
	}
}
