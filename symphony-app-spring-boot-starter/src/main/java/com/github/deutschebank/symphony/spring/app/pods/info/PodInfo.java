package com.github.deutschebank.symphony.spring.app.pods.info;
/**
 * Looks something like this:
 * 
 * <pre>
 * {
 *	"appId" : "cert-app-auth-example",
 *	"companyId" : "your pod ID / company ID",
 *	"eventType" : "appEnabled",
 *	"payload" : {
 *		"agentUrl" : "https://your.agent.domain:443",
 *		"podUrl" : "https://your.agent.domain:443",
 *		"sessionAuthUrl" : "https://your.pod.domain:8444"
 *	}
 *}</pre>
 * @author Rob Moffat
 *
 */
public class PodInfo {
	
	public static class Payload {
		
		private String agentUrl;
		private String podUrl;
		private String sessionAuthUrl;
		
		public String getAgentUrl() {
			return agentUrl;
		}
		public void setAgentUrl(String agentUrl) {
			this.agentUrl = agentUrl;
		}
		public String getPodUrl() {
			return podUrl;
		}
		public void setPodUrl(String podUrl) {
			this.podUrl = podUrl;
		}
		public String getSessionAuthUrl() {
			return sessionAuthUrl;
		}
		public void setSessionAuthUrl(String sessionAuthUrl) {
			this.sessionAuthUrl = sessionAuthUrl;
		}
	}

	private String appId;
	private String companyId;
	private String eventType;
	private Payload payload;
	private Boolean useProxy;
	
	public Boolean getUseProxy() {
		return useProxy;
	}
	public void setUseProxy(Boolean useProxy) {
		this.useProxy = useProxy;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	
	
}
