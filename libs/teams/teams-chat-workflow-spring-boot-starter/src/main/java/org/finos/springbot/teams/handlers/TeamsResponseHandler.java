package org.finos.springbot.teams.handlers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.history.StorageIDResponseHandler;
import org.finos.springbot.teams.history.TeamsHistory;
import org.finos.springbot.teams.response.templating.EntityMarkupTemplateProvider;
import org.finos.springbot.teams.response.templating.MarkupAndEntities;
import org.finos.springbot.teams.templating.adaptivecard.AdaptiveCardTemplateProvider;
import org.finos.springbot.teams.templating.thymeleaf.ThymeleafTemplateProvider;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ErrorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.TurnContextImpl;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;
import com.microsoft.bot.schema.ConversationReference;
import com.microsoft.bot.schema.Entity;
import com.microsoft.bot.schema.ResourceResponse;
import com.microsoft.bot.schema.TextFormatTypes;
import com.oracle.truffle.api.dsl.CreateCast;

public class TeamsResponseHandler implements ResponseHandler, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsResponseHandler.class);
	
	protected AttachmentHandler attachmentHandler;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	protected EntityMarkupTemplateProvider messageTemplater;
	protected AdaptiveCardTemplateProvider workTemplater;
	protected ThymeleafTemplateProvider displayTemplater;
	protected TeamsHistory teamsHistory;
	protected BotFrameworkAdapter bfa;
	protected MicrosoftAppCredentials mac;
	protected ChannelAccount botAccount;
	
	public TeamsResponseHandler( 
			AttachmentHandler attachmentHandler,
			EntityMarkupTemplateProvider messageTemplater,
			AdaptiveCardTemplateProvider workTemplater,
			ThymeleafTemplateProvider displayTemplater, 
			TeamsHistory th, 
			BotFrameworkAdapter bfa, 
			MicrosoftAppCredentials mac,
			ChannelAccount botAccount) {
		this.attachmentHandler = attachmentHandler;
		this.messageTemplater = messageTemplater;
		this.workTemplater = workTemplater;
		this.displayTemplater = displayTemplater;
		this.teamsHistory = th;
		this.bfa = bfa;
		this.mac = mac;
		this.botAccount = botAccount;
	}
	
	protected void initErrorHandler() {
		if (eh == null) {
			eh = ctx.getBean(ErrorHandler.class);
		}
	}	
	
	enum TemplateType { ADAPTIVE_CARD, THYMELEAF };

	@Override
	public void accept(Response t) {
		
		if (t.getAddress() instanceof TeamsAddressable) {		

			try {
				if (t instanceof MessageResponse) {
					Object attachment = null;
					MarkupAndEntities mae = messageTemplater.template((MessageResponse) t);
					String content = mae.getContents();
					List<Entity> entities = mae.getEntities();
					
					if (t instanceof AttachmentResponse) {
						attachment = attachmentHandler.formatAttachment((AttachmentResponse) t);
					}
					
					sendXMLResponse(content, attachment, (TeamsAddressable) t.getAddress(), entities, ((MessageResponse)t).getData());
					
				} else if (t instanceof WorkResponse) {
					WorkResponse wr = (WorkResponse) t;
					TemplateType tt = getTemplateType(wr);
 					 
					if (tt == TemplateType.ADAPTIVE_CARD) {
						JsonNode cardJson = workTemplater.template(wr);
						sendCardResponse(cardJson, (TeamsAddressable) t.getAddress(), wr.getData());
					} else {
						MarkupAndEntities mae = displayTemplater.template(wr);
						String content = mae.getContents();
						List<Entity> entities = mae.getEntities();
						sendXMLResponse(content, null, (TeamsAddressable) t.getAddress(), entities, wr.getData());
					}
				}
			} catch (Exception e) {
				throw new TeamsException("Couldn't handle response " +t, e);
			}
		}
	}

	protected TemplateType getTemplateType(WorkResponse wr) {
		TemplateType tt;
		if (displayTemplater.hasTemplate(wr)) {
			tt = TemplateType.THYMELEAF;
		} else if (workTemplater.hasTemplate(wr)) {
			tt = TemplateType.ADAPTIVE_CARD;
		} else if (requiresAdaptiveCard(wr)) {
			tt = TemplateType.ADAPTIVE_CARD;
		} else {
			tt = TemplateType.THYMELEAF;
		}
		
		return tt;
	}

	private boolean requiresAdaptiveCard(WorkResponse wr) {
		return wr.getMode() == WorkMode.EDIT || ThymeleafTemplateProvider.needsButtons(wr);
	}

	protected void sendXMLResponse(String xml, Object attachment, TeamsAddressable address, List<Entity> entities, Map<String, Object> data) throws Exception {		
		Activity out = Activity.createMessageActivity();
		out.setEntities(entities);
		out.setTextFormat(TextFormatTypes.XML);
		out.setText(xml);
		
		handleActivity(out, address).handle((rr, e) -> {
			if (e != null) {
				LOG.error(e.getMessage());
				LOG.error("message:\n"+xml);
				initErrorHandler();
				eh.handleError(e);	
			} else {
				performStorage(address, data);
			}
			
			return null;
		});
		
	}

	protected ChannelAccount getBotAccount() {
		return botAccount;
	}

	protected CompletableFuture<?> handleActivity(Activity activity, TeamsAddressable to) throws Exception {
		TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
		
		ConversationReference cr;
		
		if (ctx != null) {
			cr = ctx.getActivity().getConversationReference();
		} else {
			cr = createConversationReference(to);
		}
		
		return bfa.continueConversation(mac.getAppId(), cr, tc -> {
			return tc.sendActivity(activity)
					.thenApply(a -> null);
		});
		
	}

	protected void sendCardResponse(JsonNode json, TeamsAddressable address, Map<String, Object> data) throws Exception {		
		Activity out = Activity.createMessageActivity();
		Attachment body = new Attachment();
		body.setContentType("application/vnd.microsoft.card.adaptive");
		body.setContent(json);
		out.getAttachments().add(body);
		
		handleActivity(out, address).handle((rr, e) -> {
			if (e != null) {
				LOG.error(e.getMessage());
				try {
					LOG.error("json:\n"+new ObjectMapper().writeValueAsString(json));
				} catch (JsonProcessingException e1) {
				}
				initErrorHandler();
				eh.handleError(e);	
			} else {
				performStorage(address, data);
			}
			
			return null;
		});
		
	}

	private ConversationReference createConversationReference(TeamsAddressable address) {
		ConversationAccount ca = new ConversationAccount(address.getKey());
		ca.setTenantId(mac.getChannelAuthTenant());
		ca.setConversationType("personal");
//		out.setConversation(ca);
//		out.setChannelData(mac.getChannelAuthTenant());
//		out.setChannelId("msteams");
//		out.setFrom(botAccount);
//		out.setServiceUrl("https://smba.trafficmanager.net/uk/");
//		out.setType("message");
//		
		ConversationReference cr = new ConversationReference();
		cr.setBot(botAccount);
		cr.setConversation(ca);
		cr.setServiceUrl("https://smba.trafficmanager.net/uk/");
		cr.setLocale("en-GB");
		cr.setUser(new ChannelAccount(address.getKey()));
		cr.setChannelId("msteams");
//		cr.setActivityId(UUID.randomUUID().toString());
		return cr;
	}

	protected void performStorage(TeamsAddressable address, Map<String, Object> data) {
		if (data.containsKey(StorageIDResponseHandler.STORAGE_ID_KEY)) {
			teamsHistory.store((String) data.get(StorageIDResponseHandler.STORAGE_ID_KEY), address, data);
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