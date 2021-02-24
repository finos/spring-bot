package example.symphony.rss.feed;

import org.finos.symphony.toolkit.workflow.java.Work;

@Work(editable = true, instructions = "Enter the atom/rss feed URL here", name="Subscribe")
public class SubscribeRequest {

	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
