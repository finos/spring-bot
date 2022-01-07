package org.finos.springbot.tools.rssbot.alerter;

import org.finos.springbot.tools.rssbot.feed.Article;
import org.finos.springbot.workflow.content.Addressable;

public interface ArticleSender {

	/**
	 * Returns 1 if the article got sent.
	 */
	public int post(Addressable a, Article b);
	
}
