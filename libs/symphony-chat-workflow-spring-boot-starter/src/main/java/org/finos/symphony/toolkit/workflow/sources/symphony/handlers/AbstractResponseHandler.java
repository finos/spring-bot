package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public abstract class AbstractResponseHandler<T> implements ResponseHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractResponseHandler.class);

	@Override
	public void accept(Response t) {
		String templateName = t.getTemplateName();

		T template = StringUtils.hasText(templateName) ? getTemplateForName(templateName) : null;

		if (template == null) {
			LOG.info("Reverting to default template for " + t);
			template = getDefaultTemplate(t);
		}
		
		Content content = null;
		byte[] attachment = null;
		EntityJson data = null;
		
		if (t instanceof MessageResponse) {
			content = ((MessageResponse) t).getMessage();
		}
		
		if (t instanceof AttachmentResponse) {
			attachment = ((AttachmentResponse) t).getAttachment();
		}
		
		if (t instanceof DataResponse) {
			data = ((DataResponse) t).getData();
		}
		
		
		sendResponse(template, content, attachment, data, t.getAddress());
	}

	protected abstract void sendResponse(T template, Content content, Object attachment, EntityJson data, Addressable address);

	protected abstract T getDefaultTemplate(Response t);

	/**
	 * Load up a response-handler-specific template for a given name.
	 */
	public abstract T getTemplateForName(String name);
}
