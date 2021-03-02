package org.finos.symphony.rssbot.alerter;

import org.finos.symphony.rssbot.feed.FeedList;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public interface Alerter {

	public int allItems(Addressable a, FeedList fl);
	
}
