package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.ContentWriter;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.AbstractResponseHandler;
import org.finos.symphony.toolkit.workflow.response.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.response.handlers.DataHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.symphony.api.agent.MessagesApi;

public class SymphonyResponseHandler2 extends AbstractResponseHandler<String> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractResponseHandler.class);
	
	private ResourceLoader rl;
	private String templatePrefix = "classpath:/templates/symphony/";
	private String templateSuffix = ".ftl";
	MessagesApi messagesApi;

	public SymphonyResponseHandler2(ContentWriter contentWriter, DataHandler dataHandler,
			AttachmentHandler attachmentHandler, ResourceLoader rl, MessagesApi messagesApi) {
		super(contentWriter, dataHandler, attachmentHandler);
		this.messagesApi = messagesApi;
		this.rl = rl;
	}

	@Override
	protected void sendResponse(String template, String content, Object attachment, String data, Addressable address) {
		if (address instanceof SymphonyAddressable) {
			String streamId = ((SymphonyAddressable) address).getStreamId();
			messagesApi.v4StreamSidMessageCreatePost(null, streamId, template, data, null, attachment, null, null);
		} 
	}

	@Override
	protected String getDefaultTemplate() {
		return getTemplateForName("default");
	}

	@Override
	public String getTemplateForName(String name) {
		if (FormResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(name)) {
			
		} else if (FormResponse.DEFAULT_FORM_TEMPLATE_vIEW.equals(name)) {
			
			
		} else {
			try {
				return resolveTemplate(name);
			} catch (Exception e) {
				AbstractResponseHandler.
			}
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

}
