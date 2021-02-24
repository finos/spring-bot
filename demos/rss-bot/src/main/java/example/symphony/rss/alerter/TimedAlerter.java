package example.symphony.rss.alerter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.rometools.rome.feed.synd.SyndEntry;

import example.symphony.rss.feed.Article;
import example.symphony.rss.feed.Feed;
import example.symphony.rss.feed.FeedList;

@Component
public class TimedAlerter implements Alerter {
	
	public static Logger LOG =  LoggerFactory.getLogger(TimedAlerter.class);
	
	private Map<Addressable, FeedList> knownFeeds = new HashMap<Addressable, FeedList>();
	
	@Autowired
	ResponseHandler responseHandler;
	
	@Autowired
	EntityJsonConverter converter;

	@Lazy
	@Autowired
	Workflow w;
	
	@Override
	public void setFeeds(Addressable a, FeedList fl) {
		knownFeeds.put(a, fl);
	}
	
	@Scheduled(cron = "0 0 * * * MON-FRI")
	public void everyWeekdayHour() {
		// do something
		LOG.info("TimedAlerter waking");
	}

	public void allItems(Addressable a) {
		FeedList fl = knownFeeds.get(a);
		if (fl == null) {
			responseHandler.accept(new ErrorResponse(w, a, "No feeds set up in this conversation"));
		} else {
			allItems(a, fl);
		}
	}

	public void allItems(Addressable a, FeedList fl) {
		for (Feed f : fl.getFeeds()) {
			try {
				allItems(f, a);
			} catch (Exception e) {
				responseHandler.accept(new ErrorResponse(w, a, "Problem with feed: "+f.getName()+": "+e.getMessage()));

			}
		}
	}

	public void allItems(Feed f, Addressable a) throws Exception {
		for (SyndEntry e : f.downloadFeedItems()) {
			EntityJson ej = new EntityJson();
			Article article = new Article(e.getTitle(), e.getAuthor(), e.getUpdatedDate(), e.getUri());
			String titleStr = "<a href=\"" + article.getUri()+"\">" + article.getTitle()+"</a>";
			String description = f.getName()+ " | <i>"+article.getAuthor()+"</i>";
			
			ej.put(EntityJsonConverter.WORKFLOW_001, article);
			responseHandler.accept(new MessageResponse(w, a, ej, titleStr, description, ""));
		}
	}
	
}
