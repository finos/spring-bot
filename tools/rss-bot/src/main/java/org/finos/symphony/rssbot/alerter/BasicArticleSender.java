package org.finos.symphony.rssbot.alerter;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.rssbot.feed.Article;

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
