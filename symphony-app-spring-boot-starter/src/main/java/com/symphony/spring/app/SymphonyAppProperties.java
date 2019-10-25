package com.symphony.spring.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.symphony.spring.api.properties.IdentityProperties;
import com.symphony.spring.api.properties.ProxyProperties;

@ConfigurationProperties("symphony.app")
public class SymphonyAppProperties {
	/**
	 * Determines whether the app will perform the circle-of-trust.
	 * By default, this is off.  FULL and SNATCH_SERVER work if your app is deployed in the pod.
	 * Because apps are a pain to debug when running on the pod, you can deploy an app with SNATCH_SERVER 
	 * on the pod, and run locally with FULL.  This will allow you to test circle-of-trust functionality on your local pc.
	 */ 
	public enum CircleOfTrust { OFF, FULL, SNATCH_SERVER};

	private String groupId = "testgroup";
	private String name = "Test Application";
	private String description = "Describe your application with the property symphony.app.description";
 	private String apiKey = "secret";
 	private String publisher = "";
 	private List<String> permissions = Collections.emptyList();
 	private String baseUrl = "";
	private String allowOrigins = "";
	private String appPath = "/symphony-app";
	private IdentityProperties identity;
	private List<String> services = Arrays.asList("modules" , "applications-nav", "ui", "share", "extended-user-info");
	private boolean jwt = false;
	private String controllerJavascript = "/symphony-app/starter-include.js";
	private String controllerPath;
	private CircleOfTrust circleOfTrust = CircleOfTrust.OFF;
	private String iconPath;

	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public String getControllerPath() {
		return controllerPath;
	}
	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}
	
	public CircleOfTrust getCircleOfTrust() {
		return circleOfTrust;
	}
	public void setCircleOfTrust(CircleOfTrust circleOfTrust) {
		this.circleOfTrust = circleOfTrust;
	}
	
	public String getControllerJavascript() {
		return controllerJavascript;
	}
	public void setControllerJavascript(String controllerJavascript) {
		this.controllerJavascript = controllerJavascript;
	}

	public boolean isJwt() {
		return jwt;
	}
	public void setJwt(boolean jwt) {
		this.jwt = jwt;
	}
	
	private ProxyProperties proxy;

	public ProxyProperties getProxy() {
		return proxy;
	}
	public void setProxy(ProxyProperties proxy) {
		this.proxy = proxy;
	}
	public List<String> getServices() {
		return services;
	}
	public void setServices(List<String> services) {
		this.services = services;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}
	public String getAllowOrigins() {
		return allowOrigins;
	}
	public void setAllowOrigins(String allowOrigins) {
		this.allowOrigins = allowOrigins;
	}
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	public IdentityProperties getIdentity() {
		return identity;
	}
	public void setIdentity(IdentityProperties appIdentityDetails) {
		this.identity = appIdentityDetails;
	}

}
