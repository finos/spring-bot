package org.finos.symphony.toolkit.spring.api.properties;

public class PodProperties {

	private String id;
	private EndpointProperties pod, sessionAuth, keyAuth, agent, relay, login;

	public static enum AuthMethod { RSA, CERT }
	
	AuthMethod authMethod;
	
	public AuthMethod getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		this.authMethod = authMethod;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public EndpointProperties getPod() {
		return pod;
	}
	public void setPod(EndpointProperties pod) {
		this.pod = pod;
	}
	public EndpointProperties getSessionAuth() {
		return sessionAuth;
	}
	public void setSessionAuth(EndpointProperties sessionAuth) {
		this.sessionAuth = sessionAuth;
	}
	public EndpointProperties getKeyAuth() {
		return keyAuth;
	}
	public void setKeyAuth(EndpointProperties keyAuth) {
		this.keyAuth = keyAuth;
	}
	public EndpointProperties getAgent() {
		return agent;
	}
	public void setAgent(EndpointProperties agent) {
		this.agent = agent;
	}
	public EndpointProperties getRelay() {
		return relay;
	}
	public void setRelay(EndpointProperties relay) {
		this.relay = relay;
	}
	public EndpointProperties getLogin() {
		return login;
	}
	public void setLogin(EndpointProperties login) {
		this.login = login;
	}
}
