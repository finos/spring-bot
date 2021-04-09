package org.finos.symphony.webhookbot.domain;

public class Filter {

	enum Type { INCLUDE, EXCLUDE }
	
	private Type type;
	
	private String jsonFieldPath;
	
	private String matcher;

	public String getMatcher() {
		return matcher;
	}

	public void setMatcher(String matcher) {
		this.matcher = matcher;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getJsonFieldPath() {
		return jsonFieldPath;
	}

	public void setJsonFieldPath(String jsonFieldPath) {
		this.jsonFieldPath = jsonFieldPath;
	}

	
}
