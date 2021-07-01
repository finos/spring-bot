package org.finos.symphony.rssbot.feed;

import org.finos.symphony.toolkit.workflow.annotations.Work;

@Work(editable = true, instructions = "Enter the atom/rss feed URL here", name="RSS URL")
public class SubscribeRequest {

	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
