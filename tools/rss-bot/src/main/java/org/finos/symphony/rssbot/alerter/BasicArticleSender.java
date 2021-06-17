package org.finos.symphony.rssbot.alerter;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;

public class BasicArticleSender implements ArticleSender {

	Workflow w;	
	ResponseHandler responseHandler;
	
	public BasicArticleSender(Workflow w, ResponseHandler responseHandler) {
		super();
		this.w = w;
		this.responseHandler = responseHandler;
	}

	@Override
	public int post(Addressable a, Article article) {
		postInner(a, article);
		return 1;
	}

	protected void postInner(Addressable a, Article article) {
		EntityJson ej = new EntityJson();
		ej.put(EntityJsonConverter.WORKFLOW_001, article);
		responseHandler.accept(new FormResponse(w, a, ej, article.getFeedName(), article.getAuthor(), article, false, w.gatherButtons(article, a)));
	}

}
