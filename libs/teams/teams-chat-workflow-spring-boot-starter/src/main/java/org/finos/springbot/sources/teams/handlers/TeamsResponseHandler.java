package org.finos.springbot.sources.teams.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.finos.springbot.sources.teams.content.TeamsAddressable;
import org.finos.springbot.sources.teams.handlers.FormMessageMLConverter.Mode;
import org.finos.springbot.sources.teams.messages.TeamsXMLWriter;
import org.finos.springbot.sources.teams.turns.CurrentTurnContext;
import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.DataResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.ErrorHandler;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.Serialization;
import com.microsoft.bot.schema.TextFormatTypes;

public class TeamsResponseHandler implements ResponseHandler, ApplicationContextAware {
	
	public static final String MESSAGE_AREA = "<!-- Message Content -->";
	
	enum Format { 
		
		XML, CARD;
		
		String getExtension() {
			switch (this) {
			case XML: 
				return ".xml";
			case CARD:
			default:
				return ".json";
			}
		}
		
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsResponseHandler.class);
	
	private String templatePrefix = "classpath:/templates/teams/";
	
	protected TeamsXMLWriter contentWriter;
	protected DataHandler dataHandler;
	protected AttachmentHandler attachmentHandler;
	protected ResourceLoader rl;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	
	public TeamsResponseHandler(
			TeamsXMLWriter contentWriter, 
			DataHandler dataHandler,
			AttachmentHandler attachmentHandler, 
			ResourceLoader rl) {
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
		
		if (t.getAddress() instanceof TeamsAddressable) {		

			TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
			
			if (ctx == null) {
				return;
			}

			if (t instanceof MessageResponse) {

				Object attachment = null;
				String data = null;
				String template = null;

				template = buildTemplate((DataResponse) t, Format.XML);
				
				if (template == null) {
					template = MESSAGE_AREA;
				}
				
				if (t instanceof AttachmentResponse) {
					attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
				}
				
				sendXMLResponse(template, ((MessageResponse) t).getMessage(), attachment, (TeamsAddressable) t.getAddress(), ctx);
				
			} else if (t instanceof WorkResponse) {
				//sendCardResponse(template, attachment, data, null, ctx);
				
			}
		}
	}


	
	protected String buildTemplate(DataResponse t, Format f) {
		String templateName = t.getTemplateName();

		String template = StringUtils.hasText(templateName) ? getTemplateForName(templateName, f) : null;

		if (template == null) {
			LOG.info("Reverting to default template for " + t);
			template = getDefaultTemplate(t, f);
			LOG.info("Template: \n"+template);
		}
		
		return template;
	}

	protected void sendXMLResponse(String template, Content c, Object attachment, TeamsAddressable address, TurnContext ctx) {		
		Activity out = Activity.createMessageActivity();
		String xml = contentWriter.apply(c);
		out.setText(xml);
		out.setTextFormat(TextFormatTypes.XML);
		Attachment body = new Attachment();
		body.setContentType("application/vnd.microsoft.card.adaptive");
		JsonNode json = null;
		try {
			json = Serialization.jsonToTree(template);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		body.setContent(json);
		out.getAttachments().add(body);
		ctx.sendActivity(out).handle((rr, e) -> {
			if (e != null) {
				LOG.error(e.getMessage());
				LOG.error("message:\n"+template);
				LOG.error("json:\n"+data);
				initErrorHandler();
				eh.handleError(e);	
			}
			
			return null;
		});
		
	}
	
	protected void sendCardResponse(String x, Object attachment, String data, TeamsAddressable address, TurnContext ctx) {		
		Activity out = Activity.createMessageActivity();
		out.setText("bibblidh");
		Attachment body = new Attachment();
		body.setContentType("application/vnd.microsoft.card.adaptive");
		JsonNode json = null;
		try {
			json = Serialization.jsonToTree(template);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		body.setContent(json);
		out.getAttachments().add(body);
		ctx.sendActivity(out).handle((rr, e) -> {
			if (e != null) {
				LOG.error(e.getMessage());
				LOG.error("message:\n"+template);
				LOG.error("json:\n"+data);
				initErrorHandler();
				eh.handleError(e);	
			}
			
			return null;
		});
		
	}

	protected String getDefaultTemplate(DataResponse r) {
		if (r instanceof WorkResponse) {
			String insert;
			if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
				Class<?> c = ((WorkResponse) r).getFormClass();
				insert = formMessageMLConverter.convert(c, Mode.FORM);
			} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
				Class<?> c = ((WorkResponse) r).getFormClass();
				boolean needsButtons = needsButtons(r);						
				insert = formMessageMLConverter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
			}
			
			return insert;
		}
		
		if (r instanceof MessageResponse) {
			String basic = getTemplateForName("default", Format.XML);
			String insert = "";
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
		if (r instanceof WorkResponse) {
			ButtonList bl = (ButtonList) ((WorkResponse) r).getData().get(ButtonList.KEY);
			return (bl != null) && (bl.getContents().size() > 0);
		} else {
			return false;
		}
	}

	public String getTemplateForName(String name, Format f) {
		try {
			return resolveTemplate(name, f);
		} catch (Exception e) {
			LOG.debug("Couldn't find template: "+name);
			return null;
		}
	}

	protected String resolveTemplate(String name, Format f) throws IOException {
		return StreamUtils.copyToString(
				rl.getResource(templatePrefix + name + f.getExtension()).getInputStream(),
				StandardCharsets.UTF_8);
	}

	public String getTemplatePrefix() {
		return templatePrefix;
	}

	public void setTemplatePrefix(String templatePrefix) {
		this.templatePrefix = templatePrefix;
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
