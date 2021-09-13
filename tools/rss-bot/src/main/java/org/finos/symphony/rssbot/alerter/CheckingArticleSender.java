package org.finos.symphony.rssbot.alerter;

import java.util.Optional;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;

/**
 * This avoids repeating a post by checking the history of the room.  
 */
public class CheckingArticleSender extends BasicArticleSender {
	
	History h;
	
	public CheckingArticleSender(ResponseHandlers responseHandler, History h) {
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
		Optional<Article> existing = h.getLastFromHistory(Article.class, article.getArticleHashTag(), a);
		return (existing.isPresent() && existing.get().getUri().equals(article.getUri()));
	}

	
}
