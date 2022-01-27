package org.finos.springbot.tools.rssbot.alerter;

import java.time.Instant;
import java.util.Map;

import org.finos.springbot.tools.rssbot.feed.FeedList;
import org.finos.springbot.workflow.content.Addressable;

/**
 * This reduces the number of lookups to the backing system by keeping track of what has already been reported.
 * 
 * As the name implies, this is just a cache - nothing is stored here which can't be reloaded.
 * 
 * @author moffrob
 *
 */
public interface FeedListCache {
	
	public Instant nextReportTime(FeedList fl);
	
	public void setNextReportTime(FeedList fl);
	
	public void writeFeedList(Addressable a, FeedList fl);
	
	public Map<Addressable, FeedList> getKnownFeeds();
	
}
