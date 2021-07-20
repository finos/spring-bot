package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter.Mode;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.HeaderDetails;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLWriter;
import org.finos.symphony.toolkit.workflow.sources.symphony.streams.AbstractStreamResolving;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

public class SymphonyResponseHandler extends AbstractStreamResolving implements ResponseHandler {
	
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
	
	
	public SymphonyResponseHandler(
			MessagesApi messagesApi,
			StreamsApi streamsApi, 
			UsersApi usersApi,
			FormMessageMLConverter formMessageMLConverter, MessageMLWriter contentWriter, DataHandler dataHandler,
			AttachmentHandler attachmentHandler, ResourceLoader rl) {
		super(streamsApi, usersApi);
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
				LOG.info(template);
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
		
			if (t instanceof WorkResponse) {
				WorkResponse workResponse = (WorkResponse) t;
				HeaderDetails hd = (HeaderDetails) workResponse.getData().get(HeaderDetails.KEY);
				Object o = workResponse.getData().get(WorkResponse.OBJECT_KEY);
				if (hd == null) {
					hd = new HeaderDetails();
					workResponse.getData().put(HeaderDetails.KEY, hd);
					List<Tag> tags = new ArrayList<>(TagSupport.classHashTags(o));
					hd.setTags(tags);
				}
			}

			if (t instanceof DataResponse) {
				data = dataHandler.formatData((DataResponse) t);
			}
			
			
			sendResponse(template, attachment, data, t.getAddress());
		}
	}
	
	protected void sendResponse(String template, Object attachment, String data, Addressable address) {
		try {
			if (address instanceof SymphonyAddressable) {
				String streamId = getStreamFor((SymphonyAddressable) address);
				messagesApi.v4StreamSidMessageCreatePost(null, streamId, template, data, null, attachment, null, null);
			}
		} catch (Exception e) {
			LOG.error("Couldn/'t send message \n{} \n{}: ", template, data);
			LOG.error("Error was: ", e);
		} 
	}

	protected String getDefaultTemplate(Response r) {
		String basic = getTemplateForName("default");
		String insert = "";
		if (r instanceof WorkResponse) {
			if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
				Class<?> c = ((WorkResponse) r).getFormObject().getClass();
				insert = formMessageMLConverter.convert(c, Mode.FORM);
			} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
				Class<?> c = ((WorkResponse) r).getFormObject().getClass();
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
