package org.finos.springbot.sources.teams.handlers;

import org.finos.springbot.sources.teams.content.TeamsAddressable;
import org.finos.springbot.sources.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.springbot.workflow.response.templating.MarkupTemplateProvider;
import org.finos.springbot.workflow.response.templating.SimpleMessageMarkupTemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ErrorHandler;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.TextFormatTypes;

public class TeamsResponseHandler implements ResponseHandler, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsResponseHandler.class);
	
	protected DataHandler dataHandler;
	protected AttachmentHandler attachmentHandler;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	protected MarkupTemplateProvider xmlTemplateProvider;
	
	public TeamsResponseHandler(
			String templatePrefix,
			MarkupWriter contentWriter, 
			DataHandler dataHandler,
			AttachmentHandler attachmentHandler, 
			ResourceLoader rl) {
		this.dataHandler = dataHandler;
		this.attachmentHandler = attachmentHandler;
		this.xmlTemplateProvider = new SimpleMessageMarkupTemplateProvider(templatePrefix, ".xml", rl, contentWriter);
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
				String content = xmlTemplateProvider.template((MessageResponse) t);
				
				if (t instanceof AttachmentResponse) {
					attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
				}
				
				sendXMLResponse(content, ((MessageResponse) t).getMessage(), attachment, (TeamsAddressable) t.getAddress(), ctx);
				
			} else if (t instanceof WorkResponse) {
				//sendCardResponse(template, attachment, data, null, ctx);
				
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
	
//	protected void sendCardResponse(String x, Object attachment, String data, TeamsAddressable address, TurnContext ctx) {		
//		Activity out = Activity.createMessageActivity();
//		out.setText("bibblidh");
//		Attachment body = new Attachment();
//		body.setContentType("application/vnd.microsoft.card.adaptive");
//		JsonNode json = null;
//		try {
//			json = Serialization.jsonToTree(template);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		body.setContent(json);
//		out.getAttachments().add(body);
//		ctx.sendActivity(out).handle((rr, e) -> {
//			if (e != null) {
//				LOG.error(e.getMessage());
//				LOG.error("message:\n"+template);
//				LOG.error("json:\n"+data);
//				initErrorHandler();
//				eh.handleError(e);	
//			}
//			
//			return null;
//		});
//		
//	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
