package com.symphony.spring.api.properties;

import com.symphony.api.ConfigurableApiBuilder;

public class ProxyProperties {

	
	private String host;
	private int port = 8080;
	private String user;
	private String password;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void configure(ConfigurableApiBuilder ab) {
		if (getHost() != null) {
			ab.setProxyDetails(getHost(), getUser(), getPassword(), getPort());
		}

	}
	
	
}
