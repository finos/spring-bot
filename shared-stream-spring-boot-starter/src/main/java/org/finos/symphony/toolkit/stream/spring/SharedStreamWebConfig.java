package org.finos.symphony.toolkit.stream.spring;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.HttpMulticaster;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * This initializes shared stream http communication, but only if the 
 * user is using spring-web.
 * 
 * @author moffrob
 *
 */
@ConditionalOnClass(View.class)
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@AutoConfigureBefore({SharedStreamConfig.class})
@EnableConfigurationProperties(SymphonyStreamProperties.class)
public class SharedStreamWebConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamWebConfig.class);
	
	@Autowired
	SymphonyStreamProperties streamProperties;
	

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
	
	@Bean
	@ConditionalOnMissingBean
	public HttpClusterMessageController httpClusterMessageController(ClusterMember cm) {
		return new HttpClusterMessageController(symphonyJsonOutputView(), cm, objectMapper);
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
}
