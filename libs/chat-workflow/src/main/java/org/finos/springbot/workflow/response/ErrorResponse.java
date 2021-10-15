package org.finos.springbot.workflow.response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.finos.springbot.workflow.content.Addressable;

public class ErrorResponse extends DataResponse {
	
	public static final String ERRORS_KEY = "error";
	public static final String MESSAGE_KEY = "message";

	public ErrorResponse(Addressable stream, Throwable e) {
		this(stream, e, null);
	}
	
	public ErrorResponse(Addressable stream, Throwable e, String templateName) {
		super(stream, createEntityMap(e), templateName);
	}

	public static Map<String, Object> createEntityMap(Throwable t) {
		Map<String, Object> json = new HashMap<>();
		json.put(ERRORS_KEY, stacktraceToString(t));
		json.put(MESSAGE_KEY, t.getMessage());
		return json;
	}
	
	public static String stacktraceToString(Throwable t) {
		StringWriter out = new StringWriter(); 
		t.printStackTrace(new PrintWriter(out));
		return out.toString();
	}

	@Override
	public String toString() {
		return "ErrorResponse [getData()=" + getData() + ", getTemplateName()=" + getTemplateName() + ", getAddress()="
				+ getAddress() + "]";
	}
	
	
}
