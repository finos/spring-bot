package org.finos.symphony.rssbot.alerter;

import org.finos.symphony.rssbot.feed.Article;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;

public class BasicArticleSender implements ArticleSender {

	ResponseHandlers responseHandlers;
	
	public BasicArticleSender(ResponseHandlers responseHandler) {
		super();
		this.responseHandlers = responseHandler;
	}

	@Override
	public int post(Addressable a, Article article) {
		postInner(a, article);
		return 1;
	}

	protected void postInner(Addressable a, Article article) {
		responseHandlers.accept(new WorkResponse(a, article, WorkMode.VIEW));
	}

}
