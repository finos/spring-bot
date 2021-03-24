package org.finos.symphony.toolkit.koreai.spring;

import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;

/**
 * Holds all the properties for mapping between one KoreAI bot and one Symphony bot.
 * 
 * @author moffrob
 *
 */
public class KoreAIInstanceProperties {
	
	/**
	 * TRUE : in a room, bot responds only to messages addressed to it <br/>
	 * FALSE:  in a room, bot responds to all messages<br/>
	 * DIRECT: bot doesn't respond to messages in a room <br/>
	 */
	enum Addressed { 
		TRUE, FALSE, DIRECT 
	}

	private String jwt;

	private String url;

	private boolean skipEmptyResponses = false;
	
	private Addressed onlyAddressed = Addressed.FALSE;
	
	private IdentityProperties symphonyBot;
	
	private boolean sendErrorsToSymphony = false;
	
	private boolean sendWelcomeMessage = true;
	
	private String welcomeMessageML = "<messageML>Welcome to the chatroom!</messageML>";

	public String getWelcomeMessageML() {
		return welcomeMessageML;
	}

	public void setWelcomeMessageML(String welcomeMessageML) {
		this.welcomeMessageML = welcomeMessageML;
	}

	public boolean isSendWelcomeMessage() {
		return sendWelcomeMessage;
	}

	public void setSendWelcomeMessage(boolean sendWelcomeMessage) {
		this.sendWelcomeMessage = sendWelcomeMessage;
	}

	public boolean isSendErrorsToSymphony() {
		return sendErrorsToSymphony;
	}

	public void setSendErrorsToSymphony(boolean sendErrorsToSymphony) {
		this.sendErrorsToSymphony = sendErrorsToSymphony;
	}

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

	public Addressed isOnlyAddressed() {
		return onlyAddressed;
	}

	public void setOnlyAddressed(Addressed onlyAddressed) {
		this.onlyAddressed = onlyAddressed;
	}
	
	public String getName() {
		String email = (symphonyBot != null) ? symphonyBot.getEmail(): "unnamed";
		return email == null ? "unnamed" : email;
	}
}
