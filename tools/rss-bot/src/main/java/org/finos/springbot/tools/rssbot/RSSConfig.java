/**
 * 
 */
package org.finos.springbot.tools.rssbot;

import org.finos.springbot.symphony.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.springbot.tools.rssbot.alerter.ArticleSender;
import org.finos.springbot.tools.rssbot.alerter.CachingCheckingArticleSender;
import org.finos.springbot.tools.rssbot.alerter.FeedListCache;
import org.finos.springbot.tools.rssbot.alerter.FeedListCacheImpl;
import org.finos.springbot.tools.rssbot.load.FeedLoader;
import org.finos.springbot.tools.rssbot.notify.Notifier;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.history.History;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

@Configuration
@EnableConfigurationProperties(RSSProperties.class)
public class RSSConfig {
	
	@Autowired
	private RSSProperties properties;
	
	@Autowired
	private EntityJsonConverter ejc;
	
	public static final String WELCOME_MESSAGE = "<messageML>"
			+ "<p>Hi, welcome to <b>${entity.stream.roomName}</b></p><br />"
			+ "<p>To configure RSS feeds in this room type: <b>/subscriptions</b></p></messageML>";
	
	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, WELCOME_MESSAGE, ejc);
	}
	
	@Bean
	public FeedLoader feedLoader() {
		return new FeedLoader(properties.getProxies());
	}
	
	@Bean
	public FeedListCache feedListCache() {
		return new FeedListCacheImpl();
	}
	
	@Bean
	public Notifier notifier() {
		return new Notifier();
	}
	
	@Bean
	public ArticleSender articleSender(ResponseHandlers rh, History h) {
		return new CachingCheckingArticleSender(rh, h);
	}

}