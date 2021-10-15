package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.springbot.workflow.response.templating.MarkupTemplateProvider;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.streams.AbstractStreamResolving;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ErrorHandler;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

public class SymphonyResponseHandler extends AbstractStreamResolving implements ResponseHandler, ApplicationContextAware {
	
	public static final String MESSAGE_AREA = "<!-- Message Content -->";
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyResponseHandler.class);
	
	protected MessagesApi messagesApi;
	protected DataHandler dataHandler;
	protected AttachmentHandler attachmentHandler;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	protected MarkupTemplateProvider messageTemplater;
	protected SymphonyTemplateProvider workTemplater;
	
	
	public SymphonyResponseHandler(
			MessagesApi messagesApi,
			StreamsApi streamsApi, 
			UsersApi usersApi,
			DataHandler dataHandler,
			AttachmentHandler attachmentHandler,
			MarkupTemplateProvider messageTemplater,
			SymphonyTemplateProvider workTemplater) {
		super(streamsApi, usersApi);
		this.messagesApi = messagesApi;
		this.dataHandler = dataHandler;
		this.attachmentHandler = attachmentHandler;
		this.messageTemplater = messageTemplater;
		this.workTemplater = workTemplater;
	}

	protected void initErrorHandler() {
		if (eh == null) {
			eh = ctx.getBean(ErrorHandler.class);
		}
	}
	

	@Override
	public void accept(Response t) {
		if (t.getAddress() instanceof SymphonyAddressable) {		

			Object attachment = null;
			String data = null;
			String template = null;
			
			if (t instanceof AttachmentResponse) {
				attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
			}
		

			if (t instanceof DataResponse) {
				template = buildTemplate((DataResponse) t);
				
				if (template == null) {
					LOG.error("Cannot determine/create template for response {}", t);
					return;
				}

				data = dataHandler.formatData((DataResponse) t);
				LOG.info("JSON: \n"+ data);

				sendResponse(template, attachment, data, t.getAddress());
			}
		}
	}


	
	protected String buildTemplate(DataResponse t) {
		if (t instanceof WorkResponse) {
			return workTemplater.template((WorkResponse) t);
		} else if (t instanceof MessageResponse) {
			return messageTemplater.template((MessageResponse)t);
		} else {
			throw new UnsupportedOperationException("Can't template: "+t);
		}
	}

	protected void sendResponse(String template, Object attachment, String data, Addressable address) {
		try {
			if (address instanceof SymphonyAddressable) {
				String streamId = getStreamFor((SymphonyAddressable) address);
				messagesApi.v4StreamSidMessageCreatePost(null, streamId, template, data, null, attachment, null, null);
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
