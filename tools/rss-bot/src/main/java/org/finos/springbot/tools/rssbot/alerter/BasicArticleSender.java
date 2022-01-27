package org.finos.springbot.tools.rssbot.alerter;

import org.finos.springbot.tools.rssbot.feed.Article;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;

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
