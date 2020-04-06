package com.github.deutschebank.symphony.stream.spring;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.spring.api.SymphonyApiConfig;
import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.github.deutschebank.symphony.stream.cluster.ClusterMember;
import com.github.deutschebank.symphony.stream.cluster.RaftClusterMember;
import com.github.deutschebank.symphony.stream.cluster.transport.HttpMulticaster;
import com.github.deutschebank.symphony.stream.cluster.transport.Multicaster;
import com.github.deutschebank.symphony.stream.cluster.voting.BullyDecider;
import com.github.deutschebank.symphony.stream.cluster.voting.Decider;
import com.github.deutschebank.symphony.stream.cluster.voting.MajorityDecider;
import com.github.deutschebank.symphony.stream.filter.SymphonyLeaderEventFilter;
import com.github.deutschebank.symphony.stream.handler.SymphonyStreamHandler;
import com.github.deutschebank.symphony.stream.log.LogMessageHandler;
import com.github.deutschebank.symphony.stream.log.SharedLog;
import com.github.deutschebank.symphony.stream.log.SymphonyRoomSharedLog;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;

@ConditionalOnBean(value = StreamEventConsumer.class)
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@EnableConfigurationProperties(SymphonyStreamProperties.class)
public class SharedStreamConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamConfig.class);
	
	public static final String STREAM_EXCEPTIONS_BEAN = "SymphonyStreamExceptionsBean";

	@Autowired
	MessagesApi messagesApi;
	
	@Autowired
	DatafeedApi datafeedApi;
	
	@Autowired
	SymphonyStreamProperties streamProperties;
	
	@Autowired
	StreamEventConsumer userDefinedCallback;
	
	@Autowired
	TaskScheduler taskScheduler;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Value("${server.port:8080}")
	private String serverPort;
	
	@Bean
	@ConditionalOnMissingBean
	public SharedLog symphonySharedLog() {
		return new SymphonyRoomSharedLog(
				streamProperties.getCoordinationStreamId(), 
				messagesApi, 
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Participant selfParticipant() {
		String endpointPath = streamProperties.getEndpointPath();
		endpointPath = endpointPath.startsWith("/") ? endpointPath : "/" + endpointPath;
		String url;
		
		if (StringUtils.isEmpty(streamProperties.getInternalHostUrl())) {
			// construct URL from properties
			String hostName = hostName();
			url = "http://" + hostName +":"+serverPort +endpointPath;
		} else {
			// construct using supplied host url
			String hostAndPortUrl = streamProperties.getInternalHostUrl();
			url = hostAndPortUrl + endpointPath;
		}

		LOG.info("Cluster starting up. This participant id: "+url);
		
		return new Participant(url);	

	}

	protected String hostName() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
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
	public ParticipationNotifier participationNotifier() {
		return new ParticipationNotifier(symphonySharedLog(), 
				selfParticipant(),
				taskScheduler, 
				streamProperties.getParticipantWriteIntervalMillis());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Decider decider() {
		Participant self = selfParticipant();
		
		switch (streamProperties.getAlgorithm()) { 
		case BULLY: 	
			return new BullyDecider(self);
		case MAJORITY:
			return new MajorityDecider(() -> multicaster().getQuorumSize(), self);
		default:
			throw new IllegalArgumentException("Algorithm not found: "+streamProperties.getAlgorithm());
		} 
	}
	
	public static class SymphonyStreamUrlMapping extends SimpleUrlHandlerMapping {}

	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyStreamUrlMapping symphonyStreamUrlMapping() {
		SymphonyStreamUrlMapping out = new SymphonyStreamUrlMapping();
		Map<String, Object> sm = Collections.singletonMap(streamProperties.getEndpointPath(), httpClusterMessageController());
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
	public HttpClusterMessageController httpClusterMessageController() {
		return new HttpClusterMessageController(symphonyJsonOutputView(), clusterMember());
	}
	
	@Bean
	public ClusterMember clusterMember() {
		return new RaftClusterMember(selfParticipant(), streamProperties.getTimeoutMs(), decider(), multicaster());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyLeaderEventFilter symphonyLeaderEventFilter() {
		Multicaster mc = multicaster();
		return new SymphonyLeaderEventFilter(userDefinedCallback, false, selfParticipant(), 
			(LogMessageHandler) symphonySharedLog(), lm -> mc.accept(lm.getParticipant()));
	}
	
	@Bean(name = STREAM_EXCEPTIONS_BEAN)
	@ConditionalOnMissingBean
	public Consumer<Exception> streamExceptions() {
		return e -> LOG.error("Symphony Stream Exception occurred: ", e);
	}
	
	@Bean
	public SymphonyStreamHandler symphonyStreamHandler(@Qualifier(STREAM_EXCEPTIONS_BEAN) Consumer<Exception> exceptionHandler) {
		return new SymphonyStreamHandler(datafeedApi, symphonyLeaderEventFilter(), exceptionHandler, true);
	}
}
