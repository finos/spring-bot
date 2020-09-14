package com.github.deutschebank.symphony.workflow.response;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;

public class ErrorResponse extends MessageResponse {

	public ErrorResponse(Workflow wf, Addressable stream, String messageML) {
		super(wf, stream, null, "Error", "Please Investigate", messageML);
	}

	@Override
	public String toString() {
		return "ErrorResponse [getMessage()=" + getMessage() + ", getData()=" + getData() + ", getAddress()="
				+ getAddress() + ", getWorkflow()=" + getWorkflow() + "]";
	}

	
	
}
