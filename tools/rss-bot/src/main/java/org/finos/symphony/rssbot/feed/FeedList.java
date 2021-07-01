package org.finos.symphony.rssbot.feed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.rssbot.alerter.TimedAlerter;
import org.finos.symphony.rssbot.load.FeedLoader;
import org.finos.symphony.rssbot.notify.Notifier;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.io.FeedException;

@Work(editable = true, instructions = "Feeds being reported in this chat")
@Template(
		edit = "classpath:/feedlist-edit.ftl", 
		view = "classpath:/feedlist-view.ftl")
public class FeedList {
	
	public static final Logger LOG = LoggerFactory.getLogger(FeedList.class);


	List<Feed> feeds = new ArrayList<Feed>();
	boolean paused = false ;
	Instant lastUpdated = Instant.now();
	
	List<Filter> filters = new ArrayList<Filter>();

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public Instant getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Instant lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	@Exposed(addToHelp = true, description = "Show RSS Feeds Published In This Room", isButton = true, isMessage = true)
	public static FeedList subscriptions(Addressable a, SymphonyHistoryImpl hist, Workflow wf) {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		FeedList ob = fl.orElse(new FeedList());
		return ob;
	}

	@Exposed(addToHelp = true, description = "Subscribe to a feed. ", isButton = false, isMessage = true)
	public static Object subscribe(SubscribeRequest sr, Addressable a, SymphonyHistoryImpl hist, FeedLoader loader, Author author, Notifier n) throws Exception {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		return fl.orElseGet(() -> new FeedList()).add(sr, loader, a, author, n);
	}
	
	@Exposed(addToHelp = false, description = "Add Subscription", isButton = true, isMessage = false) 
	public Object add(SubscribeRequest sr, FeedLoader loader, Addressable a, Author author, Notifier n) throws Exception {
		try {
			Feed feed = loader.createFeed(sr.url);
			if (!this.feeds.contains(feed)) {
				this.feeds.add(feed);
			}
			this.lastUpdated = Instant.now();
			n.sendSuccessNotification(sr, a, author);
			return this;
		} catch (FeedException e) {
			n.sendFailureNotification(sr, a, e, author);
			LOG.error("Couldn't add feed: ", e);
			return null;
		}
	}

	@Exposed(addToHelp = true, description = "Stop Feeding (can be resumed later)", isButton = true, isMessage = true)
	public FeedList pause() {
		this.paused = true;
		this.lastUpdated = Instant.now();
		return this;
	}

	@Exposed(addToHelp = true, description = "Resume feeds if paused", isButton = true, isMessage = true)
	public FeedList resume() {
		this.paused = false;
		this.lastUpdated = Instant.now();
		return this;
	}
	
	@Exposed(addToHelp = true, description = "Fetch latest news now", isButton = true, isMessage = true) 
	public void latest(TimedAlerter ta) {
		ta.everyWeekdayHour();
	}
	
	@Exposed(addToHelp = true, description = "Add A New Filter", isButton = true, isMessage = true) 
	public FeedList filter(Filter f) {
		this.filters.add(f);
		return this;
	}
}
