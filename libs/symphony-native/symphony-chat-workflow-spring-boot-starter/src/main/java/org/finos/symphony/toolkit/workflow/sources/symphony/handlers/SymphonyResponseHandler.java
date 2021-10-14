package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter.Mode;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLWriter;
import org.finos.symphony.toolkit.workflow.sources.symphony.streams.AbstractStreamResolving;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ErrorHandler;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

public class SymphonyResponseHandler extends AbstractStreamResolving implements ResponseHandler, ApplicationContextAware {
	
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
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	
	public SymphonyResponseHandler(
			MessagesApi messagesApi,
			StreamsApi streamsApi, 
			UsersApi usersApi,
			FormMessageMLConverter formMessageMLConverter, 
			MessageMLWriter contentWriter, 
			DataHandler dataHandler,
			AttachmentHandler attachmentHandler, 
			ResourceLoader rl) {
		super(streamsApi, usersApi);
		this.messagesApi = messagesApi;
		this.formMessageMLConverter = formMessageMLConverter;
		this.contentWriter = contentWriter;
		this.dataHandler = dataHandler;
		this.attachmentHandler = attachmentHandler;
		this.rl = rl;
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
		String templateName = t.getTemplateName();

		String template = StringUtils.hasText(templateName) ? getTemplateForName(templateName) : null;

		if (template == null) {
			LOG.info("Reverting to default template for " + t);
			template = getDefaultTemplate(t);
			LOG.info("Template: \n"+template);
		}
		
		return basic.replaceAll(MESSAGE_AREA, insert);
		
		return template;
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

	protected String getDefaultTemplate(DataResponse r) {
		String basic = getTemplateForName("default");
		String insert = "";
		if (r instanceof WorkResponse) {
			if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
				Class<?> c = ((WorkResponse) r).getFormClass();
				insert = formMessageMLConverter.convert(c, Mode.FORM);
			} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
				Class<?> c = ((WorkResponse) r).getFormClass();
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
			
			ret
		}
	}

	protected boolean needsButtons(Response r) {
		if (r instanceof WorkResponse) {
			ButtonList bl = (ButtonList) ((WorkResponse) r).getData().get(ButtonList.KEY);
			return (bl != null) && (bl.getContents().size() > 0);
		} else {
			return false;
		}
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


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
