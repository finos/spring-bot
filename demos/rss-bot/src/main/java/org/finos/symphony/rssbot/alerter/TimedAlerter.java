package org.finos.symphony.rssbot.alerter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.rssbot.feed.Feed;
import org.finos.symphony.rssbot.feed.FeedList;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
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
import com.symphony.api.model.StreamFilter;
import com.symphony.api.model.StreamList;
import com.symphony.api.pod.StreamsApi;

@Component
public class TimedAlerter implements Alerter {
	
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
	
	@Scheduled(initialDelay = 10, fixedDelay = 1000000) // = "0 0 * * * MON-FRI")
	public void everyWeekdayHour() {
		LOG.info("TimedAlerter waking");

		StreamFilter filter = new StreamFilter();
		filter.includeInactiveStreams(false);
		int skip = 0;
		StreamList sl;
		do {
			sl = streams.v1StreamsListPost(null, null, skip, 50);
			sl.forEach(s -> handleFeed(new RoomDef("", "", false, s.getId())));
			skip += sl.size();
		} while (sl.size() == 50);
		
		LOG.info("TimedAlerter processed "+skip+" streams ");
	}

	public void handleFeed(Addressable a) {
		Optional<FeedList> fl = h.getLastFromHistory(FeedList.class, a); 
		if ((fl.isPresent()) && (!fl.get().isPaused())) {
			allItems(a, fl.get());
		}
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
		if (lastArticle.isEmpty()) {
			return false;
		} else {
			List<String> urls = lastArticle.get().getFeedUrls();
			
			if ((urls != null) && (urls.contains(f.getUrl()))) {
				return true;
			}
		}
		
		return false;
	}

	private int allItemsSince(Instant startTime, Feed f, Addressable a, Instant since, FeedList fl) throws Exception {
		int count = 0;
		for (SyndEntry e : f.downloadFeedItems()) {
			if (e.getPublishedDate().toInstant().isAfter(since)) {
				EntityJson ej = new EntityJson();
				Article article = new Article(e.getTitle(), e.getAuthor(), e.getPublishedDate().toInstant(), e.getUri(), startTime, fl);
				String titleStr = "<a href=\"" + article.getUri()+"\">" + article.getTitle()+"</a>";
				String description = f.getName()+ " | <i>"+article.getAuthor()+"</i>";
				ej.put(EntityJsonConverter.WORKFLOW_001, article);
				responseHandler.accept(new MessageResponse(w, a, ej, titleStr, description, ""));	
				count ++;
			}
			
		}
		
		return count;
	}
	
}
