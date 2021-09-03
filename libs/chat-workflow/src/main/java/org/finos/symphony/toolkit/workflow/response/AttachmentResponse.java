package org.finos.symphony.toolkit.workflow.response;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;

public class AttachmentResponse extends MessageResponse {
	
	private final byte[] attachment;
	private final String extension;
	private final String name;
	
	public AttachmentResponse(Addressable stream, Content m, String template, byte[] attachment, String name, String extension) {
		super(stream, m, template);
		this.attachment = attachment;
		this.name = name;
		this.extension = extension;
	}

	public AttachmentResponse(Addressable stream, Content m, byte[] attachment, String name, String extension) {
		super(stream, m);
		this.attachment = attachment;
		this.name = name;
		this.extension = extension;
	}
	
	public AttachmentResponse(Addressable stream, byte[] attachment, String name, String extension) {
		super(stream, Content.empty());
		this.attachment = attachment;
		this.name = name;
		this.extension = extension;
	}

	public AttachmentResponse(Addressable stream, Map<String, Object> data, Content m, String template, byte[] attachment, String name, String extension) {
		super(stream, data, m, template);
		this.attachment = attachment;
		this.name = name;
		this.extension = extension;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public String getExtension() {
		return extension;
	}

	public String getName() {
		return name;
	}

	
	
}
