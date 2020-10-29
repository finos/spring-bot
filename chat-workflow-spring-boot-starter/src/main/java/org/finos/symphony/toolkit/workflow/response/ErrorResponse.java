package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public class ErrorResponse extends MessageResponse {

	public ErrorResponse(Workflow wf, Addressable stream, String messageML) {
		super(wf, stream, new EntityJson(), "Error", "Please Investigate", messageML);
	}

	@Override
	public String toString() {
		return "ErrorResponse [getMessage()=" + getMessage() + ", getData()=" + getData() + ", getAddress()="
				+ getAddress() + ", getWorkflow()=" + getWorkflow() + "]";
	}

	
	
}
