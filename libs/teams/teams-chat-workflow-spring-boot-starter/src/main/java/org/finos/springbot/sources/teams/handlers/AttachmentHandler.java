package org.finos.springbot.sources.teams.handlers;

import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;

public interface AttachmentHandler {

	public Object formatAttachment(AttachmentResponse ar);
}
