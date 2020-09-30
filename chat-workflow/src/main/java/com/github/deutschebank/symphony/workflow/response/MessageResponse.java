package com.github.deutschebank.symphony.workflow.response;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;

public class MessageResponse extends DataResponse {
	
	private final String message;

	public MessageResponse(Workflow wf, Addressable stream, Object data, String name, String instructions, String message) {
		super(wf, stream, EntityJsonConverter.newWorkflow(data), name, instructions);
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
