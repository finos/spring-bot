package org.finos.springbot.tools.rssbot.alerter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.finos.springbot.tools.rssbot.feed.FeedList;
import org.finos.springbot.workflow.content.Addressable;

public class FeedListCacheImpl implements FeedListCache {
	
	private Map<Addressable, FeedList> contents = new HashMap<>();
	private Map<FeedList, Instant> times = new HashMap<>();

	@Override
	public Instant nextReportTime(FeedList fl) {
		return times.get(fl);
	}

	@Override
	public void setNextReportTime(FeedList fl) {
		Instant now = Instant.now();
		Instant next = now.plus(fl.getUpdateIntervalMinutes(), ChronoUnit.MINUTES);
		times.put(fl, next);
	}

	@Override
	public void writeFeedList(Addressable a, FeedList fl) {
		contents.put(a, fl);
	}

	@Override
	public Map<Addressable, FeedList> getKnownFeeds() {
		return new HashMap<>(contents);
	}

}
