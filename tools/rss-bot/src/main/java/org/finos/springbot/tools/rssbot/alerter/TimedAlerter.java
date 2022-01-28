package org.finos.springbot.tools.rssbot.alerter;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.tools.rssbot.feed.Article;
import org.finos.springbot.tools.rssbot.feed.Feed;
import org.finos.springbot.tools.rssbot.feed.FeedList;
import org.finos.springbot.tools.rssbot.feed.Filter;
import org.finos.springbot.tools.rssbot.load.FeedLoader;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.ErrorResponse;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rometools.rome.feed.synd.SyndEntry;

@Component
public class TimedAlerter {
	
	public static final long _5_MINUTES = 5 * 60 * 1000;
	
	public static Logger LOG =  LoggerFactory.getLogger(TimedAlerter.class);
		
	@Autowired
	ResponseHandlers responseHandler;
	
	@Autowired
	AllConversations r;
	
	@Autowired
	AllHistory h;
	
	@Autowired
	FeedLoader loader;
	
	@Autowired
	FeedListCache flc;
	
	@Autowired
	ArticleSender sender;
	
	/**
	 * This is to ensure the feed list cache is up-to-date with symphony
	 * @return
	 */
	@Scheduled(initialDelay = 10000, fixedRate = _5_MINUTES)
	public void warmFeedListCache() {
		onAllStreams(a -> {
			Optional<FeedList> fl = h.getLastFromHistory(FeedList.class, a); 
			if (fl.isPresent()) {
				flc.writeFeedList(a, fl.get());
			}		
			return 0;
		});
	}
	
	/**
	 * Every minute, we look to see if any feedlists need refreshing..
	 * @return
	 */
	@Scheduled(fixedRate = 60000)
	public void checkForFeedRefreshes() {
	//	if (leaderService.isLeader(self)) {
			Instant now = Instant.now();
			for (Map.Entry<Addressable, FeedList> e : flc.getKnownFeeds().entrySet()) {
				if (!e.getValue().isPaused()) {
					Instant nextReportTime = flc.nextReportTime(e.getValue());
					if ((nextReportTime == null) || (nextReportTime.isBefore(now))) {
						allItems(e.getKey(), e.getValue());
					}
				}
			}
	//	}
	}

	public int onAllStreams(Function<Addressable, Integer> action) {
		LOG.info("TimedAlerter waking");
		int[] count = { 0 };

		//if (leaderService.isLeader(self)) {
			Set<Addressable> allRooms = r.getAllAddressables();
			allRooms.stream().forEach(s -> count[0] += action.apply(s));
			LOG.info("TimedAlerter processed "+allRooms.size()+" streams ");
//		} else {
//			LOG.info("Not leader, sleeping");
//		}
		
		return count[0];
	}
	
	@Scheduled(cron = "0 0 0 4 * *")
	public void firstOfTheMonth() {
		onAllStreams(s -> pauseRunningStreams(s));
	}

	private int pauseRunningStreams(Addressable a) {
		Optional<FeedList> fl = h.getLastFromHistory(FeedList.class, a); 
		if ((fl.isPresent()) && (!fl.get().isPaused())) {
			FeedList active = fl.get();
			active.setPaused(true);
			responseHandler.accept(new WorkResponse(a, active, WorkMode.VIEW));
			flc.writeFeedList(a, active);
			return 1;
		}
		
		return 0;
	}

	public int allItems(Addressable a, FeedList fl) {
		int count = 0;
		for (Feed f : fl.getFeeds()) {
			try {
				count += allItemsInFeed(f, a, fl);
				flc.setNextReportTime(fl);
			} catch (Exception e) {
				LOG.error("AllItems failed: ", e);
				responseHandler.accept(new ErrorResponse(a, e));
			}
		}
		
		return count;
	}

	private int allItemsInFeed(Feed f, Addressable a,  FeedList fl) {
		int count = 0;
		try {
			for (SyndEntry e : loader.createSyndFeed(f).getEntries()) {
				if (passesFilter(e, fl)) {
					HashTag fht = createFeedHashTag(f);
					HashTag aht = createArticleHashTag(e.getLink());
					Article article = new Article(e.getTitle(), e.getAuthor(), f.getName(), e.getLink(), fl, fht, aht);
					count += sender.post(a, article);
				}
			}			
		} catch (Exception e) {
			LOG.error("Coulnd't process feed" + f.getName(), e);
		}
		return count;
	}
	
	private boolean passesFilter(SyndEntry e, FeedList fl) {
		for (Filter f : fl.getFilters()) {
			if (!f.test(e.getTitle())) {
				return false;
			}
		}
		
		return true;
	}


	private HashTag createArticleHashTag(String uri) {
		String hashCode = Integer.toHexString(Math.abs(uri.hashCode()));
		return new HashTag(hashCode);
	}

	Pattern p = Pattern.compile("[^\\w]");

	private HashTag createFeedHashTag(Feed f) {
		String simplified = f.getName().replaceAll("[^\\w]","");
		return new HashTag(simplified);
	}
	

	
}
