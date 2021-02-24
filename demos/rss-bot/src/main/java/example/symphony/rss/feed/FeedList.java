package example.symphony.rss.feed;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.MessageHistory;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import example.symphony.rss.alerter.Alerter;
import example.symphony.rss.alerter.TimedAlerter;

@Work(editable = true, instructions = "Feeds being reported in this chat")
public class FeedList {

	List<Feed> feeds = new ArrayList<Feed>();
	
	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	@Exposed(addToHelp = true, description = "Show RSS Feeds Published In This Room", isButton = true, isMessage = true)
	public static FeedList subscriptions(Addressable a, MessageHistory hist) {
		Optional<FeedList> fl = hist.getLastFromHistory(FeedList.class, a);
		return fl.orElse(new FeedList());
	}
	
	@Exposed(addToHelp = true, description = "Subscribe to a feed. e.g. /subscribe &lt;url&gt;", isButton = false, isMessage = true)
	public static FeedList subscribe(SubscribeRequest sr, Alerter alerter, Addressable a, MessageHistory hist) throws Exception {
		SyndFeed feed = Feed.createSyndFeed(sr.url);
		Feed f = new Feed();
		f.setName(feed.getTitle());
		f.setDescription(feed.getDescription());
		f.setUrl(sr.url);
		FeedList existing = subscriptions(a, hist);
		if (!existing.feeds.contains(f)) {
			existing.feeds.add(f);
			alerter.setFeeds(a, existing);
		}
		
		return existing;
	}
	
	@Exposed(addToHelp = true, description = "Write out all RSS Feed Items to this chat", isButton = false, isMessage = true) 
	public static void latest(Addressable a, TimedAlerter ta, FeedList fl) {
		ta.allItems(a, fl);
	}
}
