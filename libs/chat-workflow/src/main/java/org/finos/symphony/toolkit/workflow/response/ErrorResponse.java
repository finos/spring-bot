package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public class ErrorResponse extends DataResponse {
	
	public static final String ERRORS_KEY = "error";
	public static final String MESSAGE_KEY = "message";


	public ErrorResponse(Addressable stream, Throwable e, String templateName) {
		super(stream, createEntityJson(e), templateName);
	}

	public static EntityJson createEntityJson(Throwable t) {
		EntityJson json = new EntityJson();
		json.put(ERRORS_KEY, t);
		json.put(MESSAGE_KEY, t.getMessage());
		return json;
	}

	@Override
	public String toString() {
		return "ErrorResponse [getData()=" + getData() + ", getTemplateName()=" + getTemplateName() + ", getAddress()="
				+ getAddress() + "]";
	}
	
	
}
