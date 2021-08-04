package org.finos.symphony.rssbot.feed;

import java.util.List;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.Template;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

@Work
@Template(view = "classpath:/article-view.ftl")
public class Article {

	private String title;
	private String author;
	private String uri;
	private List<String> feedUrls;
	private HashTag feedHashTag;
	private HashTag articleHashTag;
	private String feedName;
	private String pubDate;
	
	public Article(String title, String author, String feedName, String uri, FeedList fl, HashTag feedHashTag, HashTag articleHashTag) {
		super();
		this.title = title;
		this.feedName = feedName;
		this.author = author;
		this.uri = uri;
		this.feedUrls = fl.feeds.stream().map(f -> f.getUrl()).collect(Collectors.toList());
		this.feedHashTag = feedHashTag;
		this.articleHashTag = articleHashTag;
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
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public String getFeedName() {
		return feedName;
	}

	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}
	
	public HashTag getArticleHashTag() {
		return articleHashTag;
	}

	public void setArticleHashTag(HashTag articleHashTag) {
		this.articleHashTag = articleHashTag;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

}
