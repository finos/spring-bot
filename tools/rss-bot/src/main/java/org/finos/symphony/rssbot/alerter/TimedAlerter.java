package org.finos.symphony.rssbot.alerter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.rssbot.feed.Feed;
import org.finos.symphony.rssbot.feed.FeedList;
import org.finos.symphony.rssbot.feed.Filter;
import org.finos.symphony.rssbot.load.FeedLoader;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.room.Rooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rometools.rome.feed.synd.SyndEntry;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamFilter;
import com.symphony.api.model.StreamList;
import com.symphony.api.pod.StreamsApi;

@Component
public class TimedAlerter {
	
	public static Logger LOG =  LoggerFactory.getLogger(TimedAlerter.class);
		
	@Autowired
	ResponseHandler responseHandler;
	
	@Autowired
	EntityJsonConverter converter;

	@Lazy
	@Autowired
	Workflow w;
	
	@Autowired
	History h;
	
	@Autowired
	Rooms rooms;
	
	@Autowired
	StreamsApi streams;
	
	@Autowired
	LeaderService leaderService;
	
	@Autowired
	Participant self;
	
	@Autowired
	FeedLoader loader;
	
	@Scheduled(cron="${symphony.rss.cron:0 0 * * * MON-FRI}")
	public int regularly() {
		int count = onAllStreams(s -> handleFeed(temporaryRoomDef(s)));
		return count;
	}

	public RoomDef temporaryRoomDef(StreamAttributes s) {
		return new RoomDef("", "", false, s.getId());
	}

	public int onAllStreams(Function<StreamAttributes, Integer> action) {
		LOG.info("TimedAlerter waking");
		int[] count = { 0 };

		if (leaderService.isLeader(self)) {
			StreamFilter filter = new StreamFilter();
			filter.includeInactiveStreams(false);
			int skip = 0;
			StreamList sl;
			do {
				sl = streams.v1StreamsListPost(null, null, skip, 50);
				sl.forEach(s -> count[0] += action.apply(s));
				skip += sl.size();
			} while (sl.size() == 50);
			
			
			LOG.info("TimedAlerter processed "+skip+" streams ");
		} else {
			LOG.info("Not leader, sleeping");
		}
		
		return count[0];
	}
	
	@Scheduled(cron = "0 0 0 4 * *")
	public void firstOfTheMonth() {
		onAllStreams(s -> pauseRunningStreams(temporaryRoomDef(s)));
	}

	private int pauseRunningStreams(Addressable a) {
		Optional<FeedList> fl = h.getLastFromHistory(FeedList.class, a); 
		if ((fl.isPresent()) && (!fl.get().isPaused())) {
			FeedList active = fl.get();
			active.setPaused(true);
			EntityJson ej = EntityJsonConverter.newWorkflow(active);
			responseHandler.accept(new FormResponse(w, a, ej, "Renew Feed Subscriptions", "Please select RESUME to continue feeds in this chat room", active, false, 
				w.gatherButtons(active, a)));
			return 1;
		}
		
		return 0;
	}

	public int handleFeed(Addressable a) {
		int count = 0;
		Optional<FeedList> fl = h.getLastFromHistory(FeedList.class, a); 
		if ((fl.isPresent()) && (!fl.get().isPaused())) {
			count += allItems(a, fl.get());
		}
		return count;
	}

	public int allItems(Addressable a, FeedList fl) {
		Optional<Article> lastArticle = h.getLastFromHistory(Article.class, a);
		Instant startTime = Instant.now();
		int count = 0;
		
		for (Feed f : fl.getFeeds()) {
			try {
				Instant since = feedCovered(lastArticle, f) ? lastArticle.get().getStartTime() : 
					LocalDateTime.now().minusYears(1).toInstant(ZoneOffset.UTC);
				
				count += allItemsSince(startTime, f, a, since, fl);
			} catch (Exception e) {
				LOG.error("AllItems failed: ", e);
				responseHandler.accept(new ErrorResponse(w, a, "Problem with feed: "+f.getName()+": "+e.getMessage()));
			}
		}
		
		return count;
	}

	public boolean feedCovered(Optional<Article> lastArticle, Feed f) {
		if (lastArticle.isPresent()) {
			List<String> urls = lastArticle.get().getFeedUrls();
			
			if ((urls != null) && (urls.contains(f.getUrl()))) {
				return true;
			}
		} 
		
		return false;
	}

	private int allItemsSince(Instant startTime, Feed f, Addressable a, Instant since, FeedList fl) {
		int count = 0;
		try {
			for (SyndEntry e : loader.createSyndFeed(f).getEntries()) {
				Date publishedDate = e.getPublishedDate();
				if ((publishedDate != null) && (publishedDate.toInstant().isAfter(since))) {
					if (passesFilter(e, fl)) {
						EntityJson ej = new EntityJson();
						HashTag ht = createHashTag(f);
						Article article = new Article(e.getTitle(), e.getAuthor(), publishedDate.toInstant(), e.getLink(), startTime, fl, ht);
						ej.put(EntityJsonConverter.WORKFLOW_001, article);
						responseHandler.accept(new FormResponse(w, a, ej, f.getName(), e.getAuthor(), article, false, w.gatherButtons(article, a)));
						count ++;
					}
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

	Pattern p = Pattern.compile("[^\\w]");

	private HashTag createHashTag(Feed f) {
		String simplified = f.getName().replaceAll("[^\\w]","");
		return new HashTagDef(simplified);
	}
	
}
