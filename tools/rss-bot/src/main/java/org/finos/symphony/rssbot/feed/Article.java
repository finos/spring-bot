package org.finos.symphony.rssbot.feed;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

@Work(editable = false, instructions = "News")
@Template(view = "classpath:/article-view.ftl")
public class Article {

	private String title;
	private String author;
	private Instant Instant;
	private String uri;
	private Instant startTime; 
	private List<String> feedUrls;
	private HashTag feedHashTag;
	
	public Article(String title, String author, Instant Instant, String uri, Instant startTime, FeedList fl, HashTag feedHashTag) {
		super();
		this.title = title;
		this.author = author;
		this.Instant = Instant;
		this.uri = uri;
		this.startTime = startTime;
		this.feedUrls = fl.feeds.stream().map(f -> f.getUrl()).collect(Collectors.toList());
		this.feedHashTag = feedHashTag;
	}

	public Article() {
		super();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Instant getInstant() {
		return Instant;
	}

	public void setInstant(Instant Instant) {
		this.Instant = Instant;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public List<String> getFeedUrls() {
		return feedUrls;
	}

	public void setFeedUrls(List<String> feedUrls) {
		this.feedUrls = feedUrls;
	}

	public HashTag getFeedHashTag() {
		return feedHashTag;
	}

	public void setFeedHashTag(HashTag feedHashTag) {
		this.feedHashTag = feedHashTag;
	}

}
