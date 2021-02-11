package org.finos.symphony.toolkit.stream.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.SymphonyRaftClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.voting.BullyDecider;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.finos.symphony.toolkit.stream.cluster.voting.MajorityDecider;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.stream.web.HealthSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.MessagesApi;

/**
 * Builds the SymphonyStreamHandlerFactory, which is capable of supporting clustering if needed.
 * 
 * @author rob@kite9.com
 *
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(SharedStreamProperties.class)
public class SharedStreamHandlerConfig {

	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamSingleBotConfig.class);
	public static final String SYMPHONY_STREAM_HANDLER_FACTORY_BEAN = "SymphonyStreamHandlerFactoryBean";
	
	@Autowired
	ExceptionConsumer ec;
	
	@Autowired
	TaskScheduler taskScheduler;
	
	@Autowired
	SharedStreamProperties streamProperties;
	
	@Autowired(required = false)
	Participant self;
	
	@Autowired(required = false)
	HealthSupplier health;
	
	@Autowired(required = false)
	Multicaster mc;
	
	private List<ClusterMember> allClusterMembers = new ArrayList<>();
	private Map<ApiInstance, SymphonyStreamHandler> created = new HashMap<>();
		
	/**
	 * This makes sure we don't "double up" creating multiple stream handlers for the same Symphony API.
	 */
	@Bean(name = SYMPHONY_STREAM_HANDLER_FACTORY_BEAN)
	@ConditionalOnMissingBean
	public SymphonyStreamHandlerFactory symphonyStreamHandlerFactory() {
		return new SymphonyStreamHandlerFactory() {

			
			@Override
			public SymphonyStreamHandler createBean(ApiInstance symphonyApi, List<StreamEventConsumer> consumers) {
				if (created.containsKey(symphonyApi)) {
					return created.get(symphonyApi);
				}
				
				SymphonyStreamHandler out = streamHandler(symphonyApi, consumers);
				created.put(symphonyApi, out);
				return out;
			}
			
			public Collection<SymphonyStreamHandler> getAllHandlers() {
				return created.values();
			}

			@Override
			public Collection<ClusterMember> allClusterMembers() {
				return allClusterMembers;
			}
		};
	}
	
	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	@ConditionalOnMissingBean
	protected SymphonyStreamHandler streamHandler(ApiInstance symphonyApi, List<StreamEventConsumer> consumers){
		SymphonyStreamHandler out = new SymphonyStreamHandler(symphonyApi, consumers, ec, false);
		String email = symphonyApi.getIdentity().getEmail();
		LOG.info("Created SymphonyStreamHandler for "+email+" with consumers "+consumers);
		
		boolean cluster = true;
		
		if (!StringUtils.hasText(streamProperties.getCoordinationStreamId())) {
			LOG.info("No clustering = symphony.stream.coordinationStreamId not set");
			cluster = false;
		} else if (mc == null) {
			LOG.info("No clustering = no multicaster available (maybe include spring-starter-web?)");
			cluster = false;
		} else if (self == null) {
			LOG.info("No clustering = participant not set (maybe include spring-starter-web?)");
			cluster = false;
		}
		
		if (cluster) {
			SymphonyRoomSharedLog sl = symphonySharedLog(symphonyApi);
			
			LOG.info("Creating cluster using "+email);
			Decider d = decider(self);
			ClusterMember cm = clusterMember(email, d, mc, self, sl, health == null ? () -> true : health);
			allClusterMembers.add(cm);
			sl.getRegisteredParticipants(self).stream().forEach(p -> cm.accept(p));
			LOG.info("Discovered cluster members: "+cm.getSizeOfCluster());
			
			participationNotifier(symphonyApi, self, taskScheduler, sl);
			
			// filter cluster events so user doesn't see them
			SymphonyLeaderEventFilter f = leaderEventFilter(sl, cm);
			out.setFilter(f);				
		} 
		
		out.start();
		return out;
	}
	

	
	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected ParticipationNotifier participationNotifier(ApiInstance api, Participant self, TaskScheduler taskScheduler, SymphonyRoomSharedLog sl) {
		LOG.info("Creating participation notifier for cluster "+api.getIdentity().getEmail());
		return new ParticipationNotifier(
				sl, 
				self,
				taskScheduler, 
				streamProperties.getParticipantWriteIntervalMillis());
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected SymphonyLeaderEventFilter leaderEventFilter(SymphonyRoomSharedLog sl, ClusterMember cm) {
		SymphonyLeaderEventFilter f = new SymphonyLeaderEventFilter( 
				streamProperties.getCoordinationStreamId() == null, // if not defined, we are leader
				self, 
				sl, 
				lm -> cm.accept(lm.getParticipant()));
		
		return f;
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected ClusterMember clusterMember(
			String name, 
			Decider d,  
			Multicaster mc, 
			Participant self, 
			SharedLog sl, 
			HealthSupplier health) {
		Random r = new Random();
		long timeoutMs = streamProperties.getTimeoutMs();
		long randComp = Math.abs(r.nextLong() % (timeoutMs / 4));
		long totalTimeout = timeoutMs + randComp;
		LOG.info("Cluster starting up. Timeout is: {} ", totalTimeout);
		ClusterMember out = new SymphonyRaftClusterMember(name, self, totalTimeout, d, mc, sl, health);
		out.startup();
		return out;
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected SymphonyRoomSharedLog symphonySharedLog(ApiInstance api) {
		if (!StringUtils.hasText(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  Please set symphony.stream.coordination-stream-id");
		}
		
		String clusterName = api.getIdentity().getEmail();
		SymphonyRoomSharedLog out = new SymphonyRoomSharedLog(
				clusterName,
				streamProperties.getCoordinationStreamId(), 
				api.getAgentApi(MessagesApi.class), 
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
		
		LOG.info("Building Shared Log for "+clusterName);
		return out;
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected Decider decider(Participant self) {
		switch (streamProperties.getAlgorithm()) { 
		case BULLY: 	
			return new BullyDecider(self);
		case MAJORITY:
			return new MajorityDecider(self);
		default:
			throw new IllegalArgumentException("Algorithm not found: "+streamProperties.getAlgorithm());
		} 
	}
}
