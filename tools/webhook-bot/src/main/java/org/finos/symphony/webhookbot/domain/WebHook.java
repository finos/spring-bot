package org.finos.symphony.webhookbot.domain;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.java.Work;

@Work
public class WebHook {
	
	enum TemplateMode { CHICLET, TABLE, CUSTOM }

	private HashTag hookId;
	private HashTag hashTag;
	private String displayName;
	private String url;
	private List<String> fields = new ArrayList<String>();
	private String template;
	private TemplateMode mode = TemplateMode.TABLE;
	private List<Filter> filters = new ArrayList<Filter>();
	
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
	
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public TemplateMode getMode() {
		return mode;
	}

	public void setMode(TemplateMode mode) {
		this.mode = mode;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	
}
