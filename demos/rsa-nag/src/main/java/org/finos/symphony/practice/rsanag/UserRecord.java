package org.finos.symphony.practice.rsanag;

import java.time.Instant;

public class UserRecord {
	
	Long botId;
	String email;
	String botDisplayName;
	Instant userLastUpdated;
	Instant keyLastUpdated;
	String explanation;
	String status;
	
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
