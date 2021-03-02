/**
 * 
 */
package org.finos.symphony.rssbot;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.rssbot.feed.Feed;
import org.finos.symphony.rssbot.feed.FeedList;
import org.finos.symphony.rssbot.feed.SubscribeRequest;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig  {

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(FeedList.class);
		wf.addClass(Feed.class);
		wf.addClass(SubscribeRequest.class);
		wf.addClass(Article.class);
		return wf;
	}
	
	
}