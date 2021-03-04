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

}
