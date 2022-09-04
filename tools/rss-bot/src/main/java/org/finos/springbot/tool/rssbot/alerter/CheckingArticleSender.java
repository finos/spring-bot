package org.finos.springbot.tool.rssbot.alerter;

import java.util.Optional;

import org.finos.springbot.tool.rssbot.feed.Article;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;

/**
 * This avoids repeating a post by checking the history of the room.  
 */
public class CheckingArticleSender extends BasicArticleSender {
	
	AllHistory h;
	
	public CheckingArticleSender(ResponseHandlers responseHandler, AllHistory h) {
		super(responseHandler);
		this.h = h;
	}

	@Override
	public int post(Addressable a, Article article) {
		if (!alreadyPosted(a, article)) {
			return super.post(a, article);
		} else {
			return 0;
		}
	}

	protected boolean alreadyPosted(Addressable a, Article article) {
		Optional<Article> existing = h.getLastFromHistory(Article.class, article.getArticleHashTag().getName(), a);
		return (existing.isPresent() && existing.get().getUri().equals(article.getUri()));
	}

	
}
