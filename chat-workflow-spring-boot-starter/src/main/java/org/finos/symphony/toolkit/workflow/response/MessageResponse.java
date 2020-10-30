package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public class MessageResponse extends DataResponse {
	
	private final String message;

	public MessageResponse(Workflow wf, Addressable stream, EntityJson data, String name, String instructions, String message) {
		super(wf, stream, data, name, instructions);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "MessageResponse [message=" + message + ", getData()=" + getData() + ", getAddress()=" + getAddress()
				+ ", getWorkflow()=" + getWorkflow() + "]";
	}
	
	
}
