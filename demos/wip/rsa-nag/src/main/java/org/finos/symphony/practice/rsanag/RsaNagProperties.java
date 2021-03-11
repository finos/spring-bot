package org.finos.symphony.practice.rsanag;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rsa-nag")
public class RsaNagProperties {

	/**
	 * Room where audit messages of actions taken go
	 */
	private String auditRoom;

	/**
	 * Max Lifetime of the RSA Key (default = 1 year)
	 */
	int maxKeyLifetime = 365;
	
	/**
	 * Time within which nag messages need to be sent.
	 */
	int nagWithin = 28;
	
	/**
	 * From: field for emails
	 */
	String emailFrom = "noreply@nowhere.com";

	/**
	 * Email template for nag messages.
	 */
	String nagTemplate = "classpath:/default-nag-template.txt";
	
	/**
	 * Email template for bot expiry messages.
	 */
	String expiryTemplate = "classpath:/default-expiry-template.txt";
	
	/**
	 * Set true if you want to send real emails.
	 */
	boolean sendEmails;
	
	/**
	 * Set true if you want to expire the out-of-date keys.
	 */
	boolean expireBots;


	public boolean isSendEmails() {
		return sendEmails;
	}

	public void setSendEmails(boolean sendEmails) {
		this.sendEmails = sendEmails;
	}

	public boolean isExpireBots() {
		return expireBots;
	}

	public void setExpireBots(boolean expireBots) {
		this.expireBots = expireBots;
	}

	public String getExpiryTemplate() {
		return expiryTemplate;
	}

	public void setExpiryTemplate(String expiryTemplate) {
		this.expiryTemplate = expiryTemplate;
	}

	public String getNagTemplate() {
		return nagTemplate;
	}

	public void setNagTemplate(String mailTemplate) {
		this.nagTemplate = mailTemplate;
	}

	public int getNagWithin() {
		return nagWithin;
	}

	public void setNagWithin(int nagWithin) {
		this.nagWithin = nagWithin;
	}

	public String getAuditRoom() {
		return auditRoom;
	}

	public void setAuditRoom(String auditRoom) {
		this.auditRoom = auditRoom;
	}
	
	public int getMaxKeyLifetime() {
		return maxKeyLifetime;
	}

	public void setMaxKeyLifetime(int maxKeyLifetime) {
		this.maxKeyLifetime = maxKeyLifetime;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
}
