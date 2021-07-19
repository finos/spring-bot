package org.finos.symphony.toolkit.stream.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.ClusterMemberImpl;
import org.finos.symphony.toolkit.stream.cluster.HealthSupplier;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.stream.cluster.Multicaster;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
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
	TaskScheduler taskScheduler;

	@Autowired
	SharedStreamProperties streamProperties;

	@Autowired(required = false)
	Participant self;

	@Autowired(required = false)
	HealthSupplier health;

	@Autowired(required = false)
	Multicaster mc;

	@Bean
	@ConditionalOnMissingBean
	public ExceptionConsumer exceptionConsumer() {
		return (e) -> LOG.error("StreamException: ", e);
	}

	private List<ClusterMember> allClusterMembers = new ArrayList<>();
	private Map<ApiInstance, SymphonyStreamHandler> created = new HashMap<>();

	/**
	 * This makes sure we don't "double up" creating multiple stream handlers for the same Symphony API.
	 */
	@Bean(name = SYMPHONY_STREAM_HANDLER_FACTORY_BEAN)
	@ConditionalOnMissingBean
	public SymphonyStreamHandlerFactory symphonyStreamHandlerFactory(ExceptionConsumer ec) {
		return new SymphonyStreamHandlerFactory() {

			@Override
			public SymphonyStreamHandler createBean(ApiInstance symphonyApi, List<StreamEventConsumer> consumers) {
				if (created.containsKey(symphonyApi)) {
					return created.get(symphonyApi);
				}

				SymphonyStreamHandler out = streamHandler(symphonyApi, consumers, ec);
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

			@Override
			public void stopAll() {
				created.values().forEach(v -> v.stop());
				created.clear();
				allClusterMembers.forEach(cm -> cm.shutdown());
				allClusterMembers.clear();
			}
		};
	}

	protected SymphonyStreamHandler streamHandler(ApiInstance symphonyApi, List<StreamEventConsumer> consumers, ExceptionConsumer ec){
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
			SharedLog ls = symphonySharedLog(symphonyApi);

			LOG.info("Creating cluster using "+email);
			ClusterMember cm = clusterMember(email, mc, self, health == null ? () -> true : health, ls);
			allClusterMembers.add(cm);
			List<Participant> registeredParticipants = ls.getRecentParticipants();
			LOG.info("Discovered cluster members: "+registeredParticipants.size());

			participationNotifier(symphonyApi, self, taskScheduler, ls);

			// filter cluster events so user doesn't see them
			SymphonyLeaderEventFilter f = leaderEventFilter(ls);
			out.setFilter(f);
		}

		out.start();
		return out;
	}



	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected ParticipationNotifier participationNotifier(ApiInstance api, Participant self, TaskScheduler taskScheduler, SharedLog sl) {
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
	protected SymphonyLeaderEventFilter leaderEventFilter(SharedLog sl) {
		SymphonyLeaderEventFilter f = new SymphonyLeaderEventFilter(self, sl);
		return f;
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected ClusterMember clusterMember(
			String name,
			Multicaster mc,
			Participant self,
			HealthSupplier health,
			LeaderService ls) {
		Random r = new Random();
		long timeoutMs = streamProperties.getTimeoutMs();
		long randComp = Math.abs(r.nextLong() % (timeoutMs / 4));
		long totalTimeout = timeoutMs + randComp;
		LOG.info("Cluster starting up. Timeout is: {} ", totalTimeout);
		ClusterMember out = new ClusterMemberImpl(name, self, totalTimeout, mc, health, ls);
		out.startup();
		return out;
	}

	private Map<ApiInstance, SymphonyRoomSharedLog> createdLogs = new HashMap<>();

	@Bean
	@ConditionalOnMissingBean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected synchronized SymphonyRoomSharedLog symphonySharedLog(ApiInstance api) {
		if (!StringUtils.hasText(streamProperties.getCoordinationStreamId())) {
			throw new IllegalArgumentException("Shared Log needs a stream ID to write to.  Please set symphony.stream.coordination-stream-id");
		}
		if (createdLogs.containsKey(api)){
			return createdLogs.get(api);
		}

		String clusterName = api.getIdentity().getEmail();
		SymphonyRoomSharedLog out = new SymphonyRoomSharedLog(
				clusterName,
				streamProperties.getCoordinationStreamId(),
				api.getAgentApi(MessagesApi.class),
				streamProperties.getEnvironmentIdentifier(),
				streamProperties.getParticipantWriteIntervalMillis());
        createdLogs.put(api, out);
		LOG.info("Building Shared Log for "+clusterName);
		return out;
	}

}
