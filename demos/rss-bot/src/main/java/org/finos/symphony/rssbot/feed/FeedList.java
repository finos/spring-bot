package org.finos.symphony.rssbot.feed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.rssbot.alerter.Alerter;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.MessageHistory;

import com.rometools.rome.feed.synd.SyndFeed;

@Work(editable = false, instructions = "Feeds being reported in this chat")
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
	public static FormResponse subscriptions(Addressable a, MessageHistory hist, Workflow wf) {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		FeedList ob = fl.orElse(new FeedList());
		return usualFeedListResponse(a, wf, ob);
	}

	public static FormResponse usualFeedListResponse(Addressable a, Workflow wf, FeedList ob) {
		EntityJson ej = EntityJsonConverter.newWorkflow(ob);
		return new FormResponse(wf, a, ej, "RSS Feeds", "Set up any RSS feeds you want published in this room", ob,
				true, createButtonList(ob));
	}

	public static ButtonList createButtonList(FeedList fl) {
		ButtonList out = new ButtonList();
		out.add(fl.isPaused() ? new Button("resume", Type.ACTION, "Resume feed")
				: new Button("pause", Type.ACTION, "Pause feed"));
		out.add(new Button("subscribe", Type.ACTION, "Add a new feed"));
		if (fl.feeds.size() > 0) {
			out.add(new Button("latest", Type.ACTION, "Post latest"));
		}
		return out;
	}

	@Exposed(addToHelp = true, description = "Subscribe to a feed. ", isButton = true, isMessage = true)
	public static FormResponse subscribe(SubscribeRequest sr, Addressable a, MessageHistory hist, Workflow wf)
			throws Exception {
		SyndFeed feed = Feed.createSyndFeed(sr.url);
		Feed f = new Feed();
		f.setName(feed.getTitle());
		f.setDescription(feed.getDescription());
		f.setUrl(sr.url);
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		FeedList existing = fl.orElse(new FeedList());
		if (!existing.feeds.contains(f)) {
			existing.feeds.add(f);
		}
		existing.lastUpdated = Instant.now();
		return usualFeedListResponse(a, wf, existing);
	}

	@Exposed(addToHelp = true, description = "Write out all new RSS Feed Items to this chat", isButton = true, isMessage = true)
	public static MessageResponse latest(Addressable a, Alerter ta, FeedList fl, Workflow wf) {
		int count = ta.allItems(a, fl);
		if (count == 0) {
			return new MessageResponse(wf, a, new EntityJson(), "No new items", "", "");
		} else {
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
}
