package org.finos.symphony.rssbot.feed;

import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;

@Work
public class SubscribeRequest {

	String url;
	
	@Display(name = "Subscription Name (Optional)")
	String name;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
