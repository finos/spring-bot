package example.symphony.rss.alerter;

import java.util.HashMap;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import example.symphony.rss.feed.FeedList;

@Component
public class TimedAlerter implements Alerter {
	
	public static Logger LOG =  LoggerFactory.getLogger(TimedAlerter.class);
	
	private Map<Addressable, FeedList> knownFeeds = new HashMap<Addressable, FeedList>();

	@Override
	public void setFeeds(Addressable a, FeedList fl) {
		knownFeeds.put(a, fl);
	}
	
	@Scheduled(cron = "0 0 * * * MON-FRI")
	public void everyWeekdayHour() {
		// do something
		LOG.info("TimedAlerter waking");
	}

}
