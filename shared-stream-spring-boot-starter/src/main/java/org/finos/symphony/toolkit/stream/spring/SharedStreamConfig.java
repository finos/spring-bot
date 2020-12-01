package org.finos.symphony.toolkit.stream.spring;

import java.util.Random;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.SymphonyRaftClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.transport.NullMulticaster;
import org.finos.symphony.toolkit.stream.cluster.voting.BullyDecider;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.finos.symphony.toolkit.stream.cluster.voting.MajorityDecider;
import org.finos.symphony.toolkit.stream.log.LocalConsoleOnlyLog;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class, SingleBotConfig.class})
@EnableConfigurationProperties(SymphonyStreamProperties.class)
@EnableScheduling
public class SharedStreamConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamConfig.class);
	
	public static final String STREAM_EXCEPTIONS_BEAN = "SymphonyStreamExceptionsBean";

	@Autowired
	SymphonyStreamProperties streamProperties;
	
	/**
	 * Used if the http multicaster can't be instantiated.
	 */
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	public Multicaster fallbackMulticaster() {
		return new NullMulticaster();
	}
	
	/**
	 * Used if the http participant can't be instantiated.
	 */
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	public Participant fallbackParticipant() {
		String url = "dummy";
		LOG.info("Cluster starting up with dummy participant (no spring-web detected) {} ",url);
		return new Participant(url);	
	}

	/**
	 * This is used if there is no coordination stream id defined. 
	 */
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	public SharedLog fallbackLocalLog() {
		return new LocalConsoleOnlyLog();
	}
	
	public static interface ExceptionConsumer extends Consumer<Exception> {};
	
	/**
	 * Used unless overridden by the client.
	 */
	@Bean
	@ConditionalOnMissingBean
	public ExceptionConsumer fallbackExceptionConsumer() {
		return e -> LOG.error("Symphony Stream Exception occurred: ", e);
	}

	
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	public ParticipationNotifier participationNotifier(SharedLog sl, Participant self, TaskScheduler taskScheduler) {
		return new ParticipationNotifier(sl, 
				self,
				taskScheduler, 
				streamProperties.getParticipantWriteIntervalMillis());
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Lazy
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
	@Lazy
	public ClusterMember clusterMember(Decider d,  Multicaster mc, Participant self, SharedLog sl, HealthEndpoint health) {
		Random r = new Random();
		long timeoutMs = streamProperties.getTimeoutMs();
		long randComp = Math.abs(r.nextLong() % (timeoutMs / 4));
		long totalTimeout = timeoutMs + randComp;
		LOG.info("Cluster starting up. Timeout is: {} ", totalTimeout);
		ClusterMember out = new SymphonyRaftClusterMember(self, totalTimeout, d, mc, sl, () -> health.health().getStatus() == Status.UP);
		out.startup();
		return out;
	}

	
	
}
