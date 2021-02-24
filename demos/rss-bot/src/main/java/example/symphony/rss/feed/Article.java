package example.symphony.rss.feed;

import java.util.Date;

import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

@Template(view = "classpath:/article.ftl")
public class Article {

	private String title;
	private String author;
	private Date date;
	private String uri;
	
	public Article(String title, String author, Date date, String uri) {
		super();
		this.title = title;
		this.author = author;
		this.date = date;
		this.uri = uri;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	
}
