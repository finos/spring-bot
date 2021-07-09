package org.finos.symphony.rssbot.alerter;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public interface ArticleSender {

	/**
	 * Returns 1 if the article got sent.
	 */
	public int post(Addressable a, Article b);
	
}
