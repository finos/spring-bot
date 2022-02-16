package org.finos.springbot.tool.rssbot.feed;

import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.Work;

@Work
@Template(
		edit = "feedlist-edit", 
		view = "feedlist-view")
public class FeedList {
	
	List<Feed> feeds = new ArrayList<Feed>();
	boolean paused = false;
	boolean adminOnly = false;
	Integer updateIntervalMinutes = 60;
	List<Filter> filters = new ArrayList<Filter>();

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	
	
	
	public boolean isAdminOnly() {
		return adminOnly;
	}

	public void setAdminOnly(boolean adminOnly) {
		this.adminOnly = adminOnly;
	}

	public Integer getUpdateIntervalMinutes() {
		return updateIntervalMinutes;
	}

	public void setUpdateIntervalMinutes(Integer updateIntervalMinutes) {
		this.updateIntervalMinutes = updateIntervalMinutes;
	}
}
