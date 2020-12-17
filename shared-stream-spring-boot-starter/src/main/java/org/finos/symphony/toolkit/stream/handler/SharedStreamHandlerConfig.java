package org.finos.symphony.toolkit.stream.handler;

import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.filter.LeaderElectionInjector;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author rob@kite9.com
 *
 */
@Configuration
public class SharedStreamHandlerConfig {

	private static final Logger LOG = LoggerFactory.getLogger(SharedStreamSingleBotConfig.class);
	
	@Autowired(required = false)
	LeaderElectionInjector injector; 
	
	@Autowired
	ExceptionConsumer ec;
	
	private boolean createdCluster = false;
	
	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SymphonyStreamHandler streamHandler(ApiInstance symphonyApi, StreamEventConsumer consumer) {
		SymphonyStreamHandler out = new SymphonyStreamHandler(symphonyApi, consumer, ec, false);
		LOG.info("Created SymphonyStreamHandler for "+symphonyApi.getIdentity().getEmail()+" with consumer "+consumer);
		
		if (!createdCluster) {
			// first bot is expected to be the cluster-manager.
			if (injector != null) {
				injector.injectLeaderElectionBehaviour(out);
			}
			createdCluster = true;
		}
		
		out.start();
		return out;
	}
	
}
