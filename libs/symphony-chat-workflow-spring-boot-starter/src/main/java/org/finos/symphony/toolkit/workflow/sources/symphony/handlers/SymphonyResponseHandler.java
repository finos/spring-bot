package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter.Mode;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.MessagesApi;

public class SymphonyResponseHandler implements ResponseHandler {
	
	public static final String MESSAGE_AREA = "<!-- Message Content -->";
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyResponseHandler.class);
	
	private String templatePrefix = "classpath:/templates/symphony/";
	private String templateSuffix = ".ftl";
	
	protected MessagesApi messagesApi;
	protected FormMessageMLConverter formMessageMLConverter;
	protected MessageMLWriter contentWriter;
	protected DataHandler dataHandler;
	protected AttachmentHandler attachmentHandler;
	protected ResourceLoader rl;
	
	
	public SymphonyResponseHandler(MessagesApi messagesApi,
			FormMessageMLConverter formMessageMLConverter, MessageMLWriter contentWriter, DataHandler dataHandler,
			AttachmentHandler attachmentHandler, ResourceLoader rl) {
		super();
		this.messagesApi = messagesApi;
		this.formMessageMLConverter = formMessageMLConverter;
		this.contentWriter = contentWriter;
		this.dataHandler = dataHandler;
		this.attachmentHandler = attachmentHandler;
		this.rl = rl;
	}


	@Override
	public void accept(Response t) {
		if (t.getAddress() instanceof SymphonyAddressable) {		
			String templateName = t.getTemplateName();
	
			String template = StringUtils.hasText(templateName) ? getTemplateForName(templateName) : null;
	
			if (template == null) {
				LOG.info("Reverting to default template for " + t);
				template = getDefaultTemplate(t);
			}
			
			if (template == null) {
				LOG.error("Cannot determine/create template for response {}", t);
				return;
			}
			
			Object attachment = null;
			String data = null;
						
			if (t instanceof AttachmentResponse) {
				attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
			}
			
			if (t instanceof DataResponse) {
				data = dataHandler.formatData((DataResponse) t);
			}
			
			
			sendResponse(template, attachment, data, t.getAddress());
		}
	}
	
	protected void sendResponse(String template, Object attachment, String data, Addressable address) {
		if (address instanceof SymphonyAddressable) {
			String streamId = ((SymphonyAddressable) address).getStreamId();
			messagesApi.v4StreamSidMessageCreatePost(null, streamId, template, data, null, attachment, null, null);
		} 
	}

	protected String getDefaultTemplate(Response r) {
		String basic = getTemplateForName("default");
		String insert = "";
		if (r instanceof FormResponse) {
			if (FormResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
				Class<?> c = ((FormResponse) r).getFormObject().getClass();
				insert = formMessageMLConverter.convert(c, Mode.FORM);
			} else if (FormResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
				Class<?> c = ((FormResponse) r).getFormObject().getClass();
				boolean needsButtons = needsButtons(r);						
				insert = formMessageMLConverter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
			}
		}
		
		if (r instanceof MessageResponse) {
			insert = contentWriter.apply(((MessageResponse) r).getMessage());
		}

		if (basic == null) {
			return insert; 
		} else {
			if (insert.startsWith("<messageML>" )) {
				insert = insert.replaceFirst("<messageML>", "").replaceFirst("</messageML>", "");
			}
			
			return basic.replace(MESSAGE_AREA, insert);
		}
	}

	protected boolean needsButtons(Response r) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getTemplateForName(String name) {
		try {
			return resolveTemplate(name);
		} catch (Exception e) {
			LOG.debug("Couldn't find template: "+name);
			return null;
		}
	}

	protected String resolveTemplate(String name) throws IOException {
		return StreamUtils.copyToString(
				rl.getResource(templatePrefix + name + templateSuffix).getInputStream(),
				StandardCharsets.UTF_8);
	}

	public String getTemplatePrefix() {
		return templatePrefix;
	}

	public void setTemplatePrefix(String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}

	public String getTemplateSuffix() {
		return templateSuffix;
	}

	public void setTemplateSuffix(String templateSuffix) {
		this.templateSuffix = templateSuffix;
	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

}
