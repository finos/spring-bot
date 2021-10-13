package org.finos.springbot.sources.teams.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.finos.springbot.sources.teams.content.TeamsAddressable;
import org.finos.springbot.sources.teams.handlers.FormMessageMLConverter.Mode;
import org.finos.springbot.sources.teams.messages.TeamsHTMLWriter;
import org.finos.springbot.sources.teams.turns.CurrentTurnContext;
import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
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

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;

public class TeamsResponseHandler implements ResponseHandler, ApplicationContextAware {
	
	public static final String MESSAGE_AREA = "<!-- Message Content -->";
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsResponseHandler.class);
	
	private String templatePrefix = "classpath:/templates/teams/";
	private String templateSuffix = ".json";
	
	protected TeamsHTMLWriter contentWriter;
	protected DataHandler dataHandler;
	protected AttachmentHandler attachmentHandler;
	protected ResourceLoader rl;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	
	public TeamsResponseHandler(
			TeamsHTMLWriter contentWriter, 
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
		TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
		
		if (ctx == null) {
			return;
		}
		
		if (t.getAddress() instanceof TeamsAddressable) {		

			Object attachment = null;
			String data = null;
			String template = null;
			
			if (t instanceof AttachmentResponse) {
				attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
			}
		

			if (t instanceof DataResponse) {
				template = "<div>hello</div>"; //buildTemplate((DataResponse) t);
				
				if (template == null) {
					LOG.error("Cannot determine/create template for response {}", t);
					return;
				}

				//data = dataHandler.formatData((DataResponse) t);

				sendResponse(template, attachment, data, (TeamsAddressable) t.getAddress(), ctx);
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
		
		return template;
	}

	protected void sendResponse(String template, Object attachment, String data, TeamsAddressable address, TurnContext ctx) {		
		Activity out = Activity.createMessageActivity();
		out.setText("sdfds");
//		Attachment body = new Attachment();
//		body.setContentType(MediaType.TEXT_HTML_VALUE);
//		body.setContent(template);
//		out.getAttachments().add(body);
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
