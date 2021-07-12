package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.workflow.content.Addressable;

/**
 * A Response that contains some JSON data to be included in the message.
 * 
 * @author rob@kite9.com
 *
 */
public class DataResponse implements Response {

	private final Object data;
	private final String templateName;
	private final Addressable to;

	public DataResponse(Addressable to, Object data, String templateName) {
		super();
		this.to = to;
		this.data = data == null ? new Object() : data;
		this.templateName = templateName;
	}

	public Object getData() {
		return data;
	}
	

	@Override
	public String toString() {
		return "DataResponse [data=" + data + ", template=" + templateName + "]";
	}

	@Override
	public String getTemplateName() {
		return templateName;
	}

	@Override
	public Addressable getAddress() {
		return to;
	}

}
