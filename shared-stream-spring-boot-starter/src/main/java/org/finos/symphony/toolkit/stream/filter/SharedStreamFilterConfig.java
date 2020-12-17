package org.finos.symphony.toolkit.stream.filter;

import java.util.Random;

import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.SymphonyRaftClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.voting.BullyDecider;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.finos.symphony.toolkit.stream.cluster.voting.MajorityDecider;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.finos.symphony.toolkit.stream.web.HealthSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.MessagesApi;


@Configuration
@EnableConfigurationProperties(SharedStreamProperties.class)
@ConditionalOnProperty(name = "symphony.stream.coordination-stream-id")
@EnableScheduling
public class SharedStreamFilterConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamFilterConfig.class);

	@Autowired
	SharedStreamProperties streamProperties;
	
	@Autowired
	Multicaster mc;
	
	@Autowired
	Participant self;
	
	@Autowired(required = false)
	HealthSupplier health;
	
	@Autowired
	TaskScheduler taskScheduler;
	
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public LeaderElectionInjector leaderElectionInjector(ApiInstance symphonyApi) {
		return new LeaderElectionInjector() {
			
			SymphonyRoomSharedLog sl = symphonySharedLog(symphonyApi);

			SymphonyLeaderEventFilter f = leaderEventFilter(sl);
			
			@Override
			public void injectLeaderElectionBehaviour(SymphonyStreamHandler h) {
				h.setFilter(h.getFilter().and(f));				
				
				LOG.info("Creating cluster using "+symphonyApi.getIdentity().getEmail());
				health = health == null ? () -> true : health;
				clusterMember(decider(self, mc), mc, self, sl, health);
				
				LOG.info("Creating participation notifier");
				participationNotifier(symphonyApi, self, taskScheduler);
			}
		};
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SymphonyLeaderEventFilter leaderEventFilter(SymphonyRoomSharedLog sl) {
		SymphonyLeaderEventFilter f = new SymphonyLeaderEventFilter( 
				streamProperties.getCoordinationStreamId() == null, // if not defined, we are leader
				self, 
				sl, 
				lm -> mc.accept(lm.getParticipant()));
		
		return f;
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Lazy
	protected ClusterMember clusterMember(Decider d,  
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
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected ParticipationNotifier participationNotifier(ApiInstance api, Participant self, TaskScheduler taskScheduler) {
		return new ParticipationNotifier(
				symphonySharedLog(api), 
				self,
				taskScheduler, 
				streamProperties.getParticipantWriteIntervalMillis());
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected SymphonyRoomSharedLog symphonySharedLog(ApiInstance api) {
		if (StringUtils.isEmpty(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  Please set symphony.stream.coordination-stream-id");
		}
		
		SymphonyRoomSharedLog out = new SymphonyRoomSharedLog(
				streamProperties.getCoordinationStreamId(), 
				api.getAgentApi(MessagesApi.class), 
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
		
		LOG.info("Building Shared Log for "+api.getIdentity().getEmail());
		return out;
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

}
