package example.symphony.rss.alerter;

import org.finos.symphony.toolkit.workflow.content.Addressable;

import example.symphony.rss.feed.FeedList;

public interface Alerter {

	public void setFeeds(Addressable a, FeedList fl);
}
