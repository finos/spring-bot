package com.symphony.integration.jira.event.v2;

import com.symphony.integration.Icon;
import com.symphony.integration.User;
import com.symphony.integration.jira.Issue;

public class State {

	public String accent;
	public String baseUrl;
	public String tokenColor;
	public Icon icon;
	public User user;
	public Issue issue;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accent == null) ? 0 : accent.hashCode());
		result = prime * result + ((baseUrl == null) ? 0 : baseUrl.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((issue == null) ? 0 : issue.hashCode());
		result = prime * result + ((tokenColor == null) ? 0 : tokenColor.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		State other = (State) obj;
		if (accent == null) {
			if (other.accent != null)
				return false;
		} else if (!accent.equals(other.accent))
			return false;
		if (baseUrl == null) {
			if (other.baseUrl != null)
				return false;
		} else if (!baseUrl.equals(other.baseUrl))
			return false;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (issue == null) {
			if (other.issue != null)
				return false;
		} else if (!issue.equals(other.issue))
			return false;
		if (tokenColor == null) {
			if (other.tokenColor != null)
				return false;
		} else if (!tokenColor.equals(other.tokenColor))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	
	

}
