package com.symphony.integration.jira;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.symphony.integration.User;

public class Issue {
	
	public String key;
	public String url;
	public String subject;
	public String description;
	@JsonInclude(value=Include.NON_NULL)
	public String status;
	@JsonInclude(value=Include.NON_NULL)
	public String action;
	public IssueType issueType;
	public Priority priority;
	public User assignee;
	public List<Label> labels;

}
 
 
