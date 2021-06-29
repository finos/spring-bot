package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;

public class MessageResponse extends DataResponse {
	
	private final Content m;
	
	public MessageResponse(Addressable stream, EntityJson data, Content m, String template) {
		super(stream, data, template);
		this.m = m;
	}
	

	public MessageResponse(Addressable stream, Content m) {
		super(stream, null, null);
		this.m = m;
	}
	

	public MessageResponse(Addressable stream, Content m, String template) {
		super(stream, null, template);
		this.m = m;
	}
	
	
	public Content getMessage() {
		return m;
	}

	@Override
	public String toString() {
		return "MessageResponse [m=" + m + ", getData()=" + getData() + ", getTemplate()=" + getTemplate()
				+ ", getAddress()=" + getAddress() + "]";
	}

	
}
