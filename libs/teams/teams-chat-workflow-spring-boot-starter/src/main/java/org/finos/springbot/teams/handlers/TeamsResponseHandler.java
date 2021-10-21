package org.finos.springbot.teams.handlers;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.springbot.workflow.response.templating.MarkupTemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ErrorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.TextFormatTypes;

public class TeamsResponseHandler implements ResponseHandler, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsResponseHandler.class);
	
	protected AttachmentHandler attachmentHandler;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	protected MarkupTemplateProvider messageTemplater;
	protected TeamsTemplateProvider workTemplater;
	
	
	public TeamsResponseHandler(
			AttachmentHandler attachmentHandler,
			MarkupTemplateProvider messageTemplater,
			TeamsTemplateProvider workTemplater) {
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
		
		if (t.getAddress() instanceof TeamsAddressable) {		

			TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
			
			if (ctx == null) {
				return;
			}

			if (t instanceof MessageResponse) {
				Object attachment = null;
				String content = messageTemplater.template((MessageResponse) t);
				
				if (t instanceof AttachmentResponse) {
					attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
				}
				
				sendXMLResponse(content, ((MessageResponse) t).getMessage(), attachment, (TeamsAddressable) t.getAddress(), ctx);
				
			} else if (t instanceof WorkResponse) {
				JsonNode cardJson = workTemplater.template((WorkResponse) t);
				sendCardResponse(cardJson, (TeamsAddressable) t.getAddress(), ctx);
			}
		}
	}

	protected void sendXMLResponse(String xml, Content c, Object attachment, TeamsAddressable address, TurnContext ctx) {		
		Activity out = Activity.createMessageActivity();
		out.setText(xml);
		out.setTextFormat(TextFormatTypes.XML);
		if (attachment != null) {
			Attachment body = new Attachment();
			out.getAttachments().add(body);
		}
		
		ctx.sendActivity(out).handle((rr, e) -> {
			if (e != null) {
				LOG.error(e.getMessage());
				LOG.error("message:\n"+xml);
				initErrorHandler();
				eh.handleError(e);	
			}
			
			return null;
		});
		
	}
	
	protected void sendCardResponse(JsonNode json, TeamsAddressable address, TurnContext ctx) {		
		Activity out = Activity.createMessageActivity();
		Attachment body = new Attachment();
		body.setContentType("application/vnd.microsoft.card.adaptive");
		body.setContent(json);
		body.setProperties("$data", json);
		out.getAttachments().add(body);
		ctx.sendActivity(out).handle((rr, e) -> {
			if (e != null) {
				LOG.error(e.getMessage());
				try {
					LOG.error("json:\n"+new ObjectMapper().writeValueAsString(json));
				} catch (JsonProcessingException e1) {
				}
				initErrorHandler();
				eh.handleError(e);	
			}
			
			return null;
		});
		
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
