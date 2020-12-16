package org.finos.symphony.toolkit.stream.spring;

import java.util.Random;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.SymphonyRaftClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.transport.NullMulticaster;
import org.finos.symphony.toolkit.stream.cluster.voting.BullyDecider;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.finos.symphony.toolkit.stream.cluster.voting.MajorityDecider;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LocalConsoleOnlyLog;
import org.finos.symphony.toolkit.stream.log.LogMessageHandler;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;

/**
 * This config applies when you are running with a single bot instance.  
 * Note that symphony-api-spring-boot-starter is capable of supporting multiple bots too.
 * 
 * @author moffrob
 *
 */
@ConditionalOnBean(name = SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@EnableConfigurationProperties(SymphonyStreamProperties.class)
@EnableScheduling
public class SharedStreamSingleBotConfig {
	
	public static final String STREAM_EXCEPTIONS_BEAN = "SymphonyStreamExceptionsBean";
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamSingleBotConfig.class);
	
	@Autowired
	SymphonyStreamProperties streamProperties;
	
	/**
	 * Instance required by this config, create if it doesn't exist.
	 */
	@Bean
	@ConditionalOnMissingBean
	protected Multicaster fallbackMulticaster() {
		return new NullMulticaster();
	}
	
	/**
	 * Instance required by this config, create if it doesn't exist.
	 */
	@Bean
	@ConditionalOnMissingBean
	protected Participant fallbackParticipant() {
		String url = "dummy";
		LOG.info("Cluster starting up with dummy participant (no spring-web detected) {} ",url);
		return new Participant(url);	
	}

	public static interface ExceptionConsumer extends Consumer<Exception> {};

	/**
	 * Used unless overridden by the client.
	 */
	@Bean
	@ConditionalOnMissingBean
	protected ExceptionConsumer fallbackExceptionConsumer() {
		return e -> LOG.error("Symphony Stream Exception occurred: ", e);
	}

	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "symphony.stream.coordination-stream-id")
	public SharedLog symphonySharedLog(MessagesApi messagesApi) {
		if (StringUtils.isEmpty(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  Please set symphony.stream.coordination-stream-id");
		}
		
		return new SymphonyRoomSharedLog(
				streamProperties.getCoordinationStreamId(), 
				messagesApi, 
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
	}
	
	/**
	 * This is used if there is no coordination stream id defined. 
	 */
	@Bean
	@ConditionalOnMissingBean	
	public SharedLog fallbackLocalLog() {
		return new LocalConsoleOnlyLog();
	}
	
	/**
	 * If we don't have a web interface, just assume health is fine for this cluster member.
	 */
	@Bean
	@ConditionalOnMissingBean
	public HealthSupplier fallbackHealthSupplier() {
		return () -> true;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyLeaderEventFilter symphonyLeaderEventFilter(Multicaster mc, Participant self, LogMessageHandler lmh, StreamEventConsumer userDefinedCallback) {
		return new SymphonyLeaderEventFilter(userDefinedCallback, 
			streamProperties.getCoordinationStreamId() == null, // if not defined, we are leader
			self, 
			lmh, lm -> mc.accept(lm.getParticipant()));
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyStreamHandler symphonyStreamHandler(ExceptionConsumer exceptionHandler, SymphonyLeaderEventFilter eventFilter, DatafeedApi datafeedApi) {
		return new SymphonyStreamHandler(datafeedApi, eventFilter, exceptionHandler, streamProperties.isStartImmediately());
	}
	
	@Bean
	@ConditionalOnMissingBean
	protected Decider decider(Participant self, Multicaster mc) {
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
	public ClusterMember clusterMember(Decider d,  
			Multicaster mc, 
			Participant self, 
			SharedLog sl, 
			HealthSupplier health) {
		Random r = new Random();
		long timeoutMs = streamProperties.getTimeoutMs();
		long randComp = Math.abs(r.nextLong() % (timeoutMs / 4));
		long totalTimeout = timeoutMs + randComp;
		LOG.info("Cluster starting up. Timeout is: {} ", totalTimeout);
		ClusterMember out = new SymphonyRaftClusterMember(self, totalTimeout, d, mc, sl, health);
		out.startup();
		return out;
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public ParticipationNotifier participationNotifier(SharedLog sl, Participant self, TaskScheduler taskScheduler) {
		return new ParticipationNotifier(sl, 
				self,
				taskScheduler, 
				streamProperties.getParticipantWriteIntervalMillis());
	}

}
