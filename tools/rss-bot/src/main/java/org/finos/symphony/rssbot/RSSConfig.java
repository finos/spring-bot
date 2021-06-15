/**
 * 
 */
package org.finos.symphony.rssbot;

import java.lang.reflect.Field;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.rssbot.feed.Feed;
import org.finos.symphony.rssbot.feed.FeedList;
import org.finos.symphony.rssbot.feed.Filter;
import org.finos.symphony.rssbot.feed.SubscribeRequest;
import org.finos.symphony.rssbot.load.FeedLoader;
import org.finos.symphony.rssbot.notify.Notifier;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rometools.rome.io.impl.DateParser;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

@Configuration
@EnableConfigurationProperties(RSSProperties.class)
public class RSSConfig implements InitializingBean {
	
	@Autowired
	RSSProperties properties;
	
	public static final String WELCOME_MESSAGE = "<messageML>"
			+ "<p>Hi, welcome to <b>${entity.stream.roomName}</b></p><br />"
			+ "<p>To configure RSS feeds in this room type: <b>/subscriptions</b></p></messageML>";

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(RSSConfig.class.getCanonicalName());
		wf.addClass(FeedList.class);
		wf.addClass(Feed.class);
		wf.addClass(SubscribeRequest.class);
		wf.addClass(Article.class);
		wf.addClass(Filter.class);
		wf.addClass(Filter.Type.class);
		return wf;
	}
	
	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, WELCOME_MESSAGE);
	}
	
	@Bean
	FeedLoader feedLoader() {
		return new FeedLoader(properties.getProxies());
	}
	
	@Bean
	public Notifier notifier() {
		return new Notifier();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if ((properties.getTimeFormats() != null) && (properties.getTimeFormats().size() > 0)) {
			Field f = DateParser.class.getDeclaredField("ADDITIONAL_MASKS");
			f.setAccessible(true);
			String[] array = (String[]) properties.getTimeFormats().toArray(new String[properties.getTimeFormats().size()]);
			f.set(null, array);
		}
	}
}