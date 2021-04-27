package org.finos.symphony.webhookbot.domain;

import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.java.Work;

@Work
public class WebHook {

	private HashTag hookId;
	private HashTag hashTag;
	private String displayName;
	private String url;
	//private List<Filter> filters = new ArrayList<Filter>();
	
	private Template template;
	private boolean active = true;
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public HashTag getHashTag() {
		return hashTag;
	}
	
	public void setHashTag(HashTag hashTag) {
		this.hashTag = hashTag;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
//	public List<Filter> getFilters() {
//		return filters;
//	}
//	
//	public void setFilters(List<Filter> filters) {
//		this.filters = filters;
//	}
	
	public Template getTemplate() {
		return template;
	}
	
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public HashTag getHookId() {
		return hookId;
	}
	
	public void setHookId(HashTag hookId) {
		this.hookId = hookId;
	}
	
}
