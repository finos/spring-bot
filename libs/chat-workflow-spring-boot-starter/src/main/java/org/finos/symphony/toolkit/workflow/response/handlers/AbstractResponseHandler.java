package org.finos.symphony.toolkit.workflow.response.handlers;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.ContentWriter;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public abstract class AbstractResponseHandler<T> implements ResponseHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractResponseHandler.class);

	protected ContentWriter contentWriter;
	protected AttachmentHandler attachmentHandler;
	protected DataHandler dataHandler;

	@Override
	public void accept(Response t) {
		String templateName = t.getTemplateName();

		T template = StringUtils.hasText(templateName) ? getTemplateForName(templateName) : null;

		if (template == null) {
			LOG.info("Reverting to default template for " + t);
			template = getDefaultTemplate();
		}
		
		String content = null;
		Object attachment = null;
		String data = null;
		
		if (t instanceof MessageResponse) {
			content = contentWriter.apply(((MessageResponse) t).getMessage());
		}
		
		if (t instanceof AttachmentResponse) {
			attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
		}
		
		if (t instanceof DataResponse) {
			data = dataHandler.formatData((DataResponse) t);
		}
		
		
		sendResponse(template, content, attachment, data, t.getAddress());
	}

	protected abstract void sendResponse(T template, String content, Object attachment, String data, Addressable address);

	public AbstractResponseHandler(ContentWriter contentWriter, DataHandler dataHandler, AttachmentHandler attachmentHandler) {
		super();
		this.dataHandler = dataHandler;
		this.contentWriter = contentWriter;
		this.attachmentHandler = attachmentHandler;
	}

	protected abstract T getDefaultTemplate();

	public abstract T getTemplateForName(String name);
}
