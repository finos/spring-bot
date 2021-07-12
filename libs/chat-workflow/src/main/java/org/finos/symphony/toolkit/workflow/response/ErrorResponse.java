package org.finos.symphony.toolkit.workflow.response;

import java.util.HashMap;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.content.Addressable;

public class ErrorResponse extends DataResponse {
	
	public static final String ERRORS_KEY = "error";
	public static final String MESSAGE_KEY = "message";


	public ErrorResponse(Addressable stream, Throwable e, String templateName) {
		super(stream, createEntityJson(e), templateName);
	}

	public static Map<String, Object> createEntityJson(Throwable t) {
		Map<String, Object> json = new HashMap<>();
		json.put(ERRORS_KEY, t);
		json.put(MESSAGE_KEY, t.getMessage());
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getData() {
		return (Map<String, Object>) super.getData();
	}

	@Override
	public String toString() {
		return "ErrorResponse [getData()=" + getData() + ", getTemplateName()=" + getTemplateName() + ", getAddress()="
				+ getAddress() + "]";
	}
	
	
}
