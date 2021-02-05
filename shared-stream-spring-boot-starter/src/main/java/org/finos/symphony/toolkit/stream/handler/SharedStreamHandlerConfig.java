package org.finos.symphony.toolkit.stream.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static final String SYMPHONY_STREAM_HANDLER_FACTORY_BEAN = "SymphonyStreamHandlerFactoryBean";
	
	
	@Autowired(required = false)
	LeaderElectionInjector injector; 
	
	@Autowired
	ExceptionConsumer ec;
	
	private Map<ApiInstance, SymphonyStreamHandler> created = new HashMap<>();
	
	public static interface SymphonyStreamHandlerFactory {
				
		public SymphonyStreamHandler createBean(ApiInstance symphonyApi, List<StreamEventConsumer> consumers);
		
		public Collection<SymphonyStreamHandler> getAllHandlers();
		
	}
	
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
		};
	}
	
	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	protected SymphonyStreamHandler streamHandler(ApiInstance symphonyApi, List<StreamEventConsumer> consumers){
		SymphonyStreamHandler out = new SymphonyStreamHandler(symphonyApi, consumers, ec, false);
		LOG.info("Created SymphonyStreamHandler for "+symphonyApi.getIdentity().getEmail()+" with consumers "+consumers);
		
		if (created.size() == 0) {
			// first bot is expected to be the cluster-manager.
			if (injector != null) {
				injector.injectLeaderElectionBehaviour(out);
			}
		}
		
		out.start();
		return out;
	}
}
