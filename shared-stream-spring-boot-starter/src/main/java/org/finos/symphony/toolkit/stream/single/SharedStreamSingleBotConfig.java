package org.finos.symphony.toolkit.stream.single;

import java.util.List;

import org.finos.symphony.toolkit.spring.api.ApiBeanRegistration;
import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.handler.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.handler.SharedStreamHandlerConfig.SymphonyStreamHandlerFactory;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.pod.SystemApi;

/**
 * This config applies when you are running with a single bot instance, and a 
 * single instance of {@link StreamEventConsumer}.
 *   
 * This is not always the case, since symphony-api-spring-boot-starter is capable of supporting multiple bots too.
 * 
 * @author moffrob
 *
 */
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@EnableConfigurationProperties(SharedStreamProperties.class)
@ConditionalOnBean(value = {ApiInstance.class, StreamEventConsumer.class}, name = SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
public class SharedStreamSingleBotConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamSingleBotConfig.class);
	
	@Autowired
	SharedStreamProperties streamProperties;	

	@Autowired
	SymphonyStreamHandlerFactory streamHandlerFactory;
	

	@Bean
	@ConditionalOnMissingBean
	public ExceptionConsumer exceptionConsumer() {
		return (e) -> LOG.error("StreamException: ", e);
	}

	@Bean(name = "SingleSymphonyStreamHandler")
	public SymphonyStreamHandler singleSymphonyStreamHandler(List<ApiInstance> symphonyApis, List<StreamEventConsumer> consumers) throws Exception {
		LOG.debug("StreamEventConsumers: "+(consumers == null ? 0 : consumers.size()));
		LOG.debug("SymphonyAPIs: "+(symphonyApis == null ? 0 : symphonyApis.size()));
		
		if ((hasOne(consumers)) && (hasOne(symphonyApis))) {
			SymphonyStreamHandler ssh = streamHandlerFactory.createBean(symphonyApis.get(0), consumers.get(0));
			return ssh;
		} else {
			LOG.debug("Not initializing SharedStreamSingleBotConfig (needs to be one of each)");
			return null;
		}
	}

	private boolean hasOne(List<?> l) {
		return (l != null) && (l.size() == 1);
	}

}
