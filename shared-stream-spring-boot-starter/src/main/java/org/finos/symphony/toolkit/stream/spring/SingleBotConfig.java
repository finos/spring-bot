package org.finos.symphony.toolkit.stream.spring;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.finos.symphony.toolkit.stream.log.LogMessageHandler;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.finos.symphony.toolkit.stream.spring.SharedStreamConfig.ExceptionConsumer;
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
import org.springframework.util.StringUtils;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;

/**
 * This config applies when you are running with a single bot instance.  Note that symphony-api-spring-boot-starter
 * is capable of supporting multiple bots too.
 * 
 * @author moffrob
 *
 */
@ConditionalOnBean(value = { StreamEventConsumer.class, MessagesApi.class, DatafeedApi.class })
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@EnableConfigurationProperties(SymphonyStreamProperties.class)
public class SingleBotConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SingleBotConfig.class);
	
	@Autowired
	SymphonyStreamProperties streamProperties;
	
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
	

	@ConditionalOnMissingBean
	public SymphonyStreamHandler symphonyStreamHandler(ExceptionConsumer exceptionHandler, SymphonyLeaderEventFilter eventFilter, DatafeedApi datafeedApi) {
		return new SymphonyStreamHandler(datafeedApi, eventFilter, exceptionHandler, streamProperties.isStartImmediately());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyLeaderEventFilter symphonyLeaderEventFilter(Multicaster mc, Participant self, LogMessageHandler lmh, StreamEventConsumer userDefinedCallback) {
		return new SymphonyLeaderEventFilter(userDefinedCallback, 
			streamProperties.getCoordinationStreamId() == null, // if not defined, we are leader
			self, 
			lmh, lm -> mc.accept(lm.getParticipant()));
	}
	
}
