package org.finos.symphony.rssbot.alerter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;

/**
 * This checks an internal cache in order to decide whether an article is already sent.
 * 
 * This reduces the number of requests to the underlying apis.
 * 
 * @author moffrob
 *
 */
public class CachingCheckingArticleSender extends CheckingArticleSender {
	
	Map<Addressable, SoftReference<Set<Article>>> alreadySent = new HashMap<>();

	public CachingCheckingArticleSender(Workflow w, ResponseHandler responseHandler, History h) {
		super(w, responseHandler, h);
	}
	

	@Override
	protected boolean alreadyPosted(Addressable a, Article article) {
		SoftReference<Set<Article>> articles = alreadySent.get(a);
		if (articles != null) {
			Set<Article> inner = articles.get();
			if (inner != null) {
				for (Article article2 : inner) {
					if (article2.getUri().equals(article.getUri())) {
						return true;
					}
				}
			}
		}
		
		boolean out = super.alreadyPosted(a, article);
		if (out) {
			recordSent(a, article);
		}
		
		return out;
	}

	@Override
	protected void postInner(Addressable a, Article article) {
		super.postInner(a, article);
		recordSent(a, article);
	}


	public void recordSent(Addressable a, Article article) {
		SoftReference<Set<Article>> inRoom = alreadySent.get(a);
		if ((inRoom == null) || (inRoom.get() == null))  {
			inRoom = new SoftReference<>(new HashSet<>());
			alreadySent.put(a, inRoom);
		}
		
		inRoom.get().add(article);
	}

	
	
}
