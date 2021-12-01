package org.finos.symphony.rssbot.alerter;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.symphony.rssbot.feed.Article;

public interface ArticleSender {

	/**
	 * Returns 1 if the article got sent.
	 */
	public int post(Addressable a, Article b);
	
}
