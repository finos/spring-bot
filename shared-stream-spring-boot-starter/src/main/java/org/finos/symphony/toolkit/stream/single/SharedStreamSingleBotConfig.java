package org.finos.symphony.toolkit.stream.single;

import java.util.List;

import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.SharedStreamProperties;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.handler.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.agent.DatafeedApi;

/**
 * This config applies when you are running with a single bot instance, and a 
 * single instance of {@link StreamEventConsumer}.
 *   
 * This is not always the case, since symphony-api-spring-boot-starter is capable of supporting multiple bots too.
 * 
 * @author moffrob
 *
 */
@ConditionalOnBean(name = SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
@EnableConfigurationProperties(SharedStreamProperties.class)
public class SharedStreamSingleBotConfig implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamSingleBotConfig.class);
	
	@Autowired
	SharedStreamProperties streamProperties;	

	@Autowired
	List<StreamEventConsumer> consumers;
	
	@Autowired
	List<ApiInstance> symphonyApis;
	
	@Autowired
	ApplicationContext ctx;
	

	@Bean
	@ConditionalOnMissingBean
	public ExceptionConsumer exceptionConsumer() {
		return (e) -> LOG.error("StreamException: ", e);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.debug("StreamEventConsumers: "+consumers.size());
		LOG.debug("SymphonyAPIs: "+symphonyApis.size());
		
		if ((consumers.size() == 1) && (symphonyApis.size() == 1)) {
			ctx.getBean(SymphonyStreamHandler.class, symphonyApis.get(0), consumers.get(0));
		} else {
			LOG.debug("Not initializing SharedStreamSingleBotConfig (needs to be one of each)");
		}
	}

}
