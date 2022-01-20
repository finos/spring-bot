package org.finos.springbot.symphony.response.handlers;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import org.finos.springbot.symphony.SymphonyException;
import org.finos.springbot.symphony.content.SymphonyAddressable;
import org.finos.springbot.symphony.conversations.StreamResolver;
import org.finos.springbot.symphony.templating.SymphonyTemplateProvider;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.data.DataHandler;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.springbot.workflow.response.templating.Markup;
import org.finos.springbot.workflow.response.templating.MarkupTemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ErrorHandler;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Attachment;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.message.model.Message.MessageBuilder;

public class SymphonyResponseHandler implements ResponseHandler, ApplicationContextAware {
		
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyResponseHandler.class);
	
	protected MessageService messagesApi;
	protected DataHandler dataHandler;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	protected MarkupTemplateProvider<Markup> messageTemplater;
	protected SymphonyTemplateProvider workTemplater;
	protected StreamResolver sr;
	
	public SymphonyResponseHandler(
			MessageService messagesApi,
			DataHandler dataHandler,
			MarkupTemplateProvider<Markup> messageTemplater,
			SymphonyTemplateProvider workTemplater,
			StreamResolver sr
			) {
		this.messagesApi = messagesApi;
		this.dataHandler = dataHandler;
		this.messageTemplater = messageTemplater;
		this.workTemplater = workTemplater;
		this.sr = sr;
	}

	protected void initErrorHandler() {
		if (eh == null) {
			eh = ctx.getBean(ErrorHandler.class);
		}
	}

	@Override
	public void accept(Response t) {
		if (t.getAddress() instanceof SymphonyAddressable) {		

			byte[] attachment = null;
			String data = null;
			String template = null;
			String filename = null;
			
			if (t instanceof AttachmentResponse) {
				AttachmentResponse ar = (AttachmentResponse) t;
				attachment = ar.getAttachment();
				filename = ar.getName() + "." + ar.getExtension();
			}
		

			if (t instanceof DataResponse) {
				template = buildTemplate((DataResponse) t);
				
				if (template == null) {
					LOG.error("Cannot determine/create template for response {}", t);
					return;
				}

				data = dataHandler.formatData((DataResponse) t);
				LOG.info("JSON: \n"+ data);

				sendResponse(template, attachment, data, t.getAddress(), filename);
			}
		}
	}
	
	protected String buildTemplate(DataResponse t) {
		if (t instanceof MessageResponse) {
			return messageTemplater.template((MessageResponse)t).getContents();
		} else if (t instanceof WorkResponse) {
			return workTemplater.template((WorkResponse) t);
		} else {
			throw new SymphonyException("Can't template: "+t);
		}
	}

	protected void sendResponse(String template, byte[] attachment, String data, Addressable address, String filename) {
		try {
			if (address instanceof SymphonyAddressable) {
				String streamId = sr.getStreamFor((SymphonyAddressable) address);
				MessageBuilder out = Message.builder().content(template).data(data);
				if (attachment != null) {
					Attachment a = new Attachment(new ByteArrayInputStream(attachment), filename);
					out = out.attachments(Collections.singletonList(a));
				}
				messagesApi.send(streamId, out.build());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("message:\n"+template);
			LOG.error("json:\n"+data);
			initErrorHandler();
			eh.handleError(e);
		}
	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
