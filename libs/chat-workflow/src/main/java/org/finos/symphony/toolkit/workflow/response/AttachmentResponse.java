package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;

public class AttachmentResponse extends MessageResponse {
	
	private final byte[] attachment;
	private final String suffix;
	private final String name;
	
	public AttachmentResponse(Addressable stream, Content m, String template, byte[] attachment, String name, String suffix) {
		super(stream, m, template);
		this.attachment = attachment;
		this.name = name;
		this.suffix = suffix;
	}

	public AttachmentResponse(Addressable stream, Content m, byte[] attachment, String name, String suffix) {
		super(stream, m);
		this.attachment = attachment;
		this.name = name;
		this.suffix = suffix;
	}
	
	public AttachmentResponse(Addressable stream, byte[] attachment, String name, String suffix) {
		super(stream, Content.empty());
		this.attachment = attachment;
		this.name = name;
		this.suffix = suffix;
	}

	public AttachmentResponse(Addressable stream, Object data, Content m, String template, byte[] attachment, String name, String suffix) {
		super(stream, data, m, template);
		this.attachment = attachment;
		this.name = name;
		this.suffix = suffix;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getName() {
		return name;
	}

	
	
}
