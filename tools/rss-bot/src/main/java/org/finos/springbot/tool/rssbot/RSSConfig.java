/**
 * 
 */
package org.finos.springbot.tool.rssbot;

import org.finos.springbot.tool.rssbot.alerter.ArticleSender;
import org.finos.springbot.tool.rssbot.alerter.CachingCheckingArticleSender;
import org.finos.springbot.tool.rssbot.alerter.FeedListCache;
import org.finos.springbot.tool.rssbot.alerter.FeedListCacheImpl;
import org.finos.springbot.tool.rssbot.load.FeedLoader;
import org.finos.springbot.tool.rssbot.notify.Notifier;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.history.History;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.welcome.RoomWelcomeEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	RoomWelcomeEventConsumer rwec(ResponseHandlers rh) {
		return new RoomWelcomeEventConsumer(rh, a -> Message.of(WELCOME_MESSAGE));
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