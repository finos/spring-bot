package org.finos.symphony.rssbot.feed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.finos.symphony.toolkit.workflow.java.Work;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Work(editable = false, instructions = "RSS Feed")
public class Feed {

	String name;
	String description;
	String url;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feed other = (Feed) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<SyndEntry> downloadFeedItems() throws Exception {
		SyndFeed feed = createSyndFeed(url);
		return feed.getEntries();
	}

	public static SyndFeed createSyndFeed(String url) throws FeedException, IOException, MalformedURLException {
		SyndFeedInput input = new SyndFeedInput();
		input.setAllowDoctypes(true);
		SyndFeed feed = input.build(new XmlReader(new URL(url)));
		return feed;
	}
}
