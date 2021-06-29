package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public class DataResponse implements Response {

	private final EntityJson data;
	private final String template;
	private final Addressable to;

	public DataResponse(Addressable to, EntityJson data, String template) {
		super();
		this.to = to;
		this.data = data == null ? new EntityJson() : data;
		this.template = template;
	}

	public EntityJson getData() {
		return data;
	}
	

	@Override
	public String toString() {
		return "DataResponse [data=" + data + ", template=" + template + "]";
	}

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public Addressable getAddress() {
		return to;
	}

}
