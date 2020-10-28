package com.github.deutschebank.symphony.stream.spring;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.spring.api.SymphonyApiConfig;
import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.github.deutschebank.symphony.stream.cluster.ClusterMember;
import com.github.deutschebank.symphony.stream.cluster.SymphonyRaftClusterMember;
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
@EnableScheduling
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
	HealthEndpoint health;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Value("${server.port:8080}")
	private String serverPort;
	
	@Bean
	@ConditionalOnMissingBean
	public SharedLog symphonySharedLog() {
		if (StringUtils.isEmpty(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  PLease set symphony.stream.coordination-stream-id");
		}
		
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
		String hostAndPort = StringUtils.isEmpty(streamProperties.getEndpointHostAndPort()) ? hostNameAndPort() : streamProperties.getEndpointHostAndPort();
		String scheme = streamProperties.getEndpointScheme().toString().toLowerCase();
		String url = scheme+"://" + hostAndPort + endpointPath;
		LOG.info("Cluster starting up. This participant id: "+url);
		return new Participant(url);	

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
	public ParticipationNotifier participationNotifier(SharedLog sl, Participant self) {
		return new ParticipationNotifier(sl, 
				self,
				taskScheduler, 
				streamProperties.getParticipantWriteIntervalMillis());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Decider decider(Participant self, Multicaster mc) {
		switch (streamProperties.getAlgorithm()) { 
		case BULLY: 	
			return new BullyDecider(self);
		case MAJORITY:
			return new MajorityDecider(() -> mc.getQuorumSize(), self);
		default:
			throw new IllegalArgumentException("Algorithm not found: "+streamProperties.getAlgorithm());
		} 
	}
	
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
	
	@Bean
	@ConditionalOnMissingBean
	public ClusterMember clusterMember(Decider d,  Multicaster mc, Participant self, SharedLog sl) {
		Random r = new Random();
		long timeoutMs = streamProperties.getTimeoutMs();
		long randComp = Math.abs(r.nextLong() % (timeoutMs / 4));
		long totalTimeout = timeoutMs + randComp;
		LOG.info("Cluster starting up. Timeout is: "+totalTimeout);
		ClusterMember out = new SymphonyRaftClusterMember(self, totalTimeout, d, mc, sl, () -> health.health().getStatus() == Status.UP);
		out.startup();
		return out;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyLeaderEventFilter symphonyLeaderEventFilter(Multicaster mc, Participant self, LogMessageHandler lmh) {
		return new SymphonyLeaderEventFilter(userDefinedCallback, false, self, 
			lmh, lm -> mc.accept(lm.getParticipant()));
	}
	
	public static interface ExceptionConsumer extends Consumer<Exception> {};
	
	@Bean
	@ConditionalOnMissingBean
	public ExceptionConsumer streamExceptions() {
		return e -> LOG.error("Symphony Stream Exception occurred: ", e);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyStreamHandler symphonyStreamHandler(ExceptionConsumer exceptionHandler, SymphonyLeaderEventFilter eventFilter) {
		return new SymphonyStreamHandler(datafeedApi, eventFilter, exceptionHandler, streamProperties.isStartImmediately());
	}
}
