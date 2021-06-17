package org.finos.symphony.rssbot.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.rssbot.alerter.FeedListCache;
import org.finos.symphony.rssbot.alerter.TimedAlerter;
import org.finos.symphony.rssbot.load.FeedLoader;
import org.finos.symphony.rssbot.notify.Notifier;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.room.Rooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
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
	boolean paused = false;
	boolean adminOnly = false;
	Integer updateIntervalMinutes = 60;
	List<Filter> filters = new ArrayList<Filter>();

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
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

	@Exposed(addToHelp = true, description = "Subscribe to a feed. ", isButton = true, isMessage = true)
	public static FeedList subscribe(SubscribeRequest sr, Addressable a, SymphonyHistoryImpl hist, FeedLoader loader, Author author, Notifier n, FeedListCache rc, Rooms r) throws Exception {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		return fl.orElseGet(() -> new FeedList()).add(sr, loader, a, author, n, rc, r);
	}
	
	@Exposed(addToHelp = false, description = "Add Subscription", isButton = true, isMessage = false) 
	public FeedList add(SubscribeRequest sr, FeedLoader loader, Addressable a, Author author, Notifier n, FeedListCache rc, Rooms r) throws Exception {
		adminCheck(author, a, r);
		try {
			Feed feed = loader.createFeed(sr.url, sr.name);
			if (!this.feeds.contains(feed)) {
				this.feeds.add(feed);
			}
			n.sendSuccessNotification(sr, a, author);
			rc.writeFeedList(a, this);
			return this;
		} catch (FeedException e) {
			n.sendFailureNotification(sr, a, e, author);
			LOG.error("Couldn't add feed: ", e);
			return null;
		}
	}

	/**
	 * Throws an exception if the author is not a room admin
	 */
	private void adminCheck(Author author, Addressable a, Rooms r) {
		if (this.adminOnly) {
			if (a instanceof Room) {
				if (!r.getRoomAdmins((Room) a).contains(author)) {
					throw new RuntimeException("You need to be admin of this room to modify the feed list");
				}
			}
		}
	}

	@Exposed(addToHelp = true, description = "Stop Feeding (can be resumed later)", isButton = true, isMessage = true)
	public FeedList pause(FeedListCache rc, Addressable a) {
		this.paused = true;
		rc.writeFeedList(a, this);
		return this;
	}

	@Exposed(addToHelp = true, description = "Resume feeds if paused", isButton = true, isMessage = true)
	public FeedList resume(FeedListCache rc, Addressable a) {
		this.paused = false;
		rc.writeFeedList(a, this);
		return this;
	}
	
	@Exposed(addToHelp = true, description = "Fetch latest news now", isButton = true, isMessage = true) 
	public void latest(TimedAlerter ta, History hist, Addressable a, Workflow wf, ResponseHandler rh) {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		if (fl.isPresent()) {
			int count = ta.allItems(a, fl.get());
			if (count == 0) {
				rh.accept(new MessageResponse(wf, a, new EntityJson(), "No New News Items", "", "All recent news has already been reported"));
			}
		}
	}
	
	@Exposed(addToHelp = true, description = "Add A New Filter", isButton = true, isMessage = true) 
	public FeedList filter(Filter f) {
		this.filters.add(f);
		return this;
	}
	
	@Exposed(addToHelp = true, description = "Only room admins can modify the feeds", isButton = true, isMessage = true) 
	public FeedList makeAdminOnly(Addressable a, Rooms r, Author author) {
		adminCheck(author, a, r);
		this.adminOnly = true;
		return this;
	}
	
	@Exposed(addToHelp = true, description = "Any room member can modify the feeds (default)", isButton = true, isMessage = true) 
	public FeedList notAdminOnly(Addressable a, Rooms r, Author author) {
		adminCheck(author, a, r);
		this.adminOnly = false;
		return this;
	}
	
	@Exposed(addToHelp = true, description = "Set the rate of refresh (in minutes) e.g. \"/every 10\"", isButton = false, isMessage = true) 
	public FeedList every(Addressable a, Rooms r, Author author, Word every, Word mins, FeedListCache rc) {
		adminCheck(author, a, r);
		Integer minInt = Integer.parseInt(mins.getText());
		this.updateIntervalMinutes = minInt;
		rc.writeFeedList(a, this);
		return this;
	}
	
	
	public boolean isAdminOnly() {
		return adminOnly;
	}

	public void setAdminOnly(boolean adminOnly) {
		this.adminOnly = adminOnly;
	}

	public Integer getUpdateIntervalMinutes() {
		return updateIntervalMinutes;
	}

	public void setUpdateIntervalMinutes(Integer updateIntervalMinutes) {
		this.updateIntervalMinutes = updateIntervalMinutes;
	}
}
