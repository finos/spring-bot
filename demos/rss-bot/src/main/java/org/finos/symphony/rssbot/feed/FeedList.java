package org.finos.symphony.rssbot.feed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.MessageHistory;

import com.rometools.rome.feed.synd.SyndFeed;

@Work(editable = true, instructions = "Feeds being reported in this chat")
@Template(edit = "classpath:/feedlist-edit.ftl", view = "classpath:/feedlist-view.ftl")
public class FeedList {

	List<Feed> feeds = new ArrayList<Feed>();
	boolean paused = false ;
	Instant lastUpdated = Instant.now();

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
	public static FeedList subscriptions(Addressable a, MessageHistory hist, Workflow wf) {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		FeedList ob = fl.orElse(new FeedList());
		return ob;
	}

	@Exposed(addToHelp = true, description = "Subscribe to a feed. ", isButton = false, isMessage = true)
	public static FeedList subscribe(SubscribeRequest sr, Addressable a, MessageHistory hist, Workflow wf) throws Exception {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		return fl.orElseGet(() -> new FeedList()).add(sr);
	}
	
	@Exposed(addToHelp = true, description = "Add Subscription", isButton = true, isMessage = false) 
	public FeedList add(SubscribeRequest sr) throws Exception {
		SyndFeed feed = Feed.createSyndFeed(sr.url);
		Feed f = new Feed();
		f.setName(feed.getTitle());
		f.setDescription(feed.getDescription());
		f.setUrl(sr.url);
		if (!this.feeds.contains(f)) {
			this.feeds.add(f);
		}
		this.lastUpdated = Instant.now();
		return this;
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
}
