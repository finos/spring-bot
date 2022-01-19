package org.finos.symphony.toolkit.spring.api.properties;

public class TrustStoreProperties {

	public enum Type { PEMS, JKS, PKCS12, INLINE_PEMS }
	
	String location;
	String password;
	Type type = Type.PEMS;
	String inlinePems;
	
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

	public String getInlinePems() {
		return inlinePems;
	}

	public void setInlinePems(String inlinePems) {
		this.inlinePems = inlinePems;
	}

	
	
}
