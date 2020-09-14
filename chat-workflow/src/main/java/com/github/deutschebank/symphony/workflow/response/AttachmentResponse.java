package com.github.deutschebank.symphony.workflow.response;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;

public class AttachmentResponse extends DataResponse {
	
	private final byte[] attachment;
	private final String suffix;
	

	public AttachmentResponse(Workflow wf, Addressable stream, String name, String instructions, byte[] attachment, String suffix) {
		super(wf, stream, null, name, instructions);
		this.attachment = attachment;
		this.suffix = suffix;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public String getSuffix() {
		return suffix;
	}

	
	
}
