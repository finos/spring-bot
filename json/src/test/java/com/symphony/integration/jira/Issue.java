package com.symphony.integration.jira;

import java.util.List;

import com.symphony.integration.User;

public class Issue {
	
	public String key;
	public String url;
	public String subject;
	public String description;
	public String status;
	public String action;
	public IssueType issueType;
	public Priority priority;
	public User assignee;
	public List<Label> labels;

}
 
 
