package org.finos.symphony.practice.rsanag.report;

import java.time.Instant;

public class UserRecord {
	
	enum Action { OK, WARN, EXPIRE }
	
	Long botId;
	String email;
	String botDisplayName;
	Instant userLastUpdated;
	Instant keyLastUpdated;
	String explanation;
	String status;
	int keyAgeDays;
	Action action;
	
	public int getKeyAgeDays() {
		return keyAgeDays;
	}
	public void setKeyAgeDays(int keyAgeDays) {
		this.keyAgeDays = keyAgeDays;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getBotId() {
		return botId;
	}
	public void setBotId(Long botId) {
		this.botId = botId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBotDisplayName() {
		return botDisplayName;
	}
	public void setBotDisplayName(String botDisplayName) {
		this.botDisplayName = botDisplayName;
	}
	public Instant getUserLastUpdated() {
		return userLastUpdated;
	}
	public void setUserLastUpdated(Instant userLastUpdated) {
		this.userLastUpdated = userLastUpdated;
	}
	public Instant getKeyLastUpdated() {
		return keyLastUpdated;
	}
	public void setKeyLastUpdated(Instant keyLastUpdated) {
		this.keyLastUpdated = keyLastUpdated;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	
}
