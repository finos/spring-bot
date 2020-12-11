package org.finos.symphony.toolkit.koreai.spring;

import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;

/**
 * Holds all the properties for mapping between one KoreAI bot and one Symphony bot.
 * 
 * @author moffrob
 *
 */
public class KoreAIInstanceProperties {

	private String jwt;

	private String url;

	private boolean skipEmptyResponses = false;
	
	private boolean onlyAddressed = false;
	
	private IdentityProperties symphonyBot;

	public IdentityProperties getSymphonyBot() {
		return symphonyBot;
	}

	public void setSymphonyBot(IdentityProperties symphonyBot) {
		this.symphonyBot = symphonyBot;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

	public boolean isSkipEmptyResponses() {
		return skipEmptyResponses;
	}

	public void setSkipEmptyResponses(boolean skipEmptyResponses) {
		this.skipEmptyResponses = skipEmptyResponses;
	}

	public boolean isOnlyAddressed() {
		return onlyAddressed;
	}

	public void setOnlyAddressed(boolean onlyAddressed) {
		this.onlyAddressed = onlyAddressed;
	}
	
	public String getName() {
		String email = (symphonyBot != null) ? symphonyBot.getEmail(): "unnamed";
		return email == null ? "unnamed" : email;
	}
}
