package org.finos.symphony.rssbot.feed;

<<<<<<< HEAD
import org.finos.symphony.toolkit.workflow.annotations.Work;
=======
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;
>>>>>>> master

@Work(editable = true, instructions = "Enter the atom/rss feed URL here", name="RSS URL")
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
