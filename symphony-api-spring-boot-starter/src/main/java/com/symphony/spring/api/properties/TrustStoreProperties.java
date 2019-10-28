package com.symphony.spring.api.properties;

public class TrustStoreProperties {

	public enum Type { PEMS, JKS, PKCS12 }
	
	String location;
	String password;
	Type type = Type.PEMS;
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
