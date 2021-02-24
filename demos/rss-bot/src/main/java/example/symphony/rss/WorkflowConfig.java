/**
 * 
 */
package example.symphony.rss;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import example.symphony.rss.feed.Article;
import example.symphony.rss.feed.Feed;
import example.symphony.rss.feed.FeedList;
import example.symphony.rss.feed.SubscribeRequest;

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