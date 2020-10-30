package org.finos.symphony.toolkit.stream.spring;

import java.util.Random;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.SymphonyRaftClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.voting.BullyDecider;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.finos.symphony.toolkit.stream.cluster.voting.MajorityDecider;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessageHandler;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
		String url = "dummy";
		LOG.info("Cluster starting up with dummy participant (no spring-web detected) "+url);
		return new Participant(url);	
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
