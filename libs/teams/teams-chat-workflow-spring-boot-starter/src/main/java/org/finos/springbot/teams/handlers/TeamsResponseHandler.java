package org.finos.springbot.teams.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.history.StorageIDResponseHandler;
import org.finos.springbot.teams.response.templating.EntityMarkupTemplateProvider;
import org.finos.springbot.teams.response.templating.MarkupAndEntities;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.finos.springbot.teams.templating.adaptivecard.AdaptiveCardTemplateProvider;
import org.finos.springbot.teams.templating.thymeleaf.ThymeleafTemplateProvider;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.springbot.workflow.tags.HeaderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ErrorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.Entity;
import com.microsoft.bot.schema.ResourceResponse;
import com.microsoft.bot.schema.TextFormatTypes;

public class TeamsResponseHandler implements ResponseHandler, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsResponseHandler.class);
	
	protected AttachmentHandler attachmentHandler;
	protected ApplicationContext ctx;
	protected ErrorHandler eh;
	protected EntityMarkupTemplateProvider messageTemplater;
	protected AdaptiveCardTemplateProvider workTemplater;
	protected ThymeleafTemplateProvider displayTemplater;
	protected TeamsStateStorage teamsState;
	protected TeamsConversations teamsConversations;
	
	public TeamsResponseHandler( 
			AttachmentHandler attachmentHandler,
			EntityMarkupTemplateProvider messageTemplater,
			AdaptiveCardTemplateProvider workTemplater,
			ThymeleafTemplateProvider displayTemplater, 
			TeamsStateStorage th, 
			TeamsConversations tc) {
		this.attachmentHandler = attachmentHandler;
		this.messageTemplater = messageTemplater;
		this.workTemplater = workTemplater;
		this.displayTemplater = displayTemplater;
		this.teamsState = th;
		this.teamsConversations = tc;
	}
	
	protected void initErrorHandler() {
		if (eh == null) {
			eh = ctx.getBean(ErrorHandler.class);
		}
	}	
	
	enum TemplateType { ADAPTIVE_CARD, THYMELEAF, BOTH };

	@Override
	public void accept(Response t) {
		
		if (t.getAddress() instanceof TeamsAddressable) {		
			TeamsAddressable ta = (TeamsAddressable) t.getAddress();
			String messageId = null;

			try {
				if (t instanceof MessageResponse) {
					MessageResponse mr = (MessageResponse)t;
					Object attachment = null;
					MarkupAndEntities mae = messageTemplater.template(mr);
					String content = mae.getContents();
					List<Entity> entities = mae.getEntities();
					
					if (t instanceof AttachmentResponse) {
						attachment = attachmentHandler.formatAttachment((AttachmentResponse) mr);
					}
					
					messageId = getOptionalMessageID(t);
					
					sendXMLResponse(content, attachment, ta, entities, mr.getData(), messageId)
						.handle(handleErrorAndStorage(content, ta, mr.getData()));
					
				} else if (t instanceof WorkResponse) {
					WorkResponse wr = (WorkResponse) t;
					TemplateType tt = getTemplateType(wr);
					messageId = getOptionalMessageID(t);

 					 
					if (tt == TemplateType.ADAPTIVE_CARD) {
						JsonNode cardJson = workTemplater.template(wr);
						sendCardResponse(cardJson, ta, wr.getData(), messageId)
							.handle(handleErrorAndStorage(cardJson, ta, wr.getData()));
						;
					} else {
						MarkupAndEntities mae = displayTemplater.template(wr);
						String content = mae.getContents();
						List<Entity> entities = mae.getEntities();
						sendXMLResponse(content, null, ta, entities, wr.getData(), messageId)
							.handle(handleButtonsIfNeeded(tt, wr, messageId))
							.handle(handleErrorAndStorage(content, ta, wr.getData()));
						
					}
				}
			} catch (Exception e) {
				throw new TeamsException("Couldn't handle response " +t, e);
			}
		}
	}

	private String getOptionalMessageID(Response t) {
		String messageId = null;
		Map<String, Object> drData = ((DataResponse) t).getData();
		if (drData != null) {
			messageId = (String) drData.get(DataResponse.MESSAGE_UPDATE_ID_KEY);
		}
		
		return messageId;
	}

	protected TemplateType getTemplateType(WorkResponse wr) {
		TemplateType tt;
		if (displayTemplater.hasTemplate(wr)) {
			tt = TemplateType.THYMELEAF;
		} else if (workTemplater.hasTemplate(wr)) {
			tt = TemplateType.ADAPTIVE_CARD;
		} else if (wr.getMode() == WorkMode.EDIT) {
			tt = TemplateType.ADAPTIVE_CARD;
		} else if (ThymeleafTemplateProvider.needsButtons(wr)) {
			tt = TemplateType.BOTH;
		} else {
			tt = TemplateType.THYMELEAF;
		}
		
		return tt;
	}

	protected CompletableFuture<ResourceResponse> sendXMLResponse(String xml, Object attachment, TeamsAddressable address, List<Entity> entities, Map<String, Object> data, String id) throws Exception {		
		Activity out = createActivity(id);
		out.setEntities(entities);
		out.setTextFormat(TextFormatTypes.XML);
		out.setText(xml);
		return teamsConversations.handleActivity(out, address);
	}

	private Activity createActivity(String id) {
		Activity out = Activity.createMessageActivity();
		out.setId(id);
		return out;
	}

	private BiFunction<? super ResourceResponse, Throwable, ResourceResponse> handleButtonsIfNeeded(TemplateType tt, WorkResponse wr, String messageId) {
		if (messageId != null) {
			// we're updating - don't write the buttons again
			return (rr, e) -> null;
		}
		return (rr, e) -> {
			try {
				if (e == null) {
					if (tt == TemplateType.BOTH) {
						// we also need to send the buttons, so clear out the form name
						WorkResponse justButtons = new WorkResponse(
								wr.getAddress(), 
								wr.getData(), 
								wr.getTemplateName(),
								wr.getMode(),
								null);
						JsonNode buttonsJson = workTemplater.template(null);
						JsonNode expandedJson = workTemplater.applyTemplate(buttonsJson, justButtons);
						return sendCardResponse(expandedJson, (TeamsAddressable) wr.getAddress(), wr.getData(), messageId).get();
					} else {
						return null;
					}

				} else {
					throw e;
				}
			} catch (Throwable e1) {
				throw new RuntimeException("Passing on exception", e);
			}
		};
	}
	
	private BiFunction<? super ResourceResponse, Throwable, ResourceResponse> handleErrorAndStorage(Object out, TeamsAddressable address, Map<String, Object> data) {
		return (rr, e) -> {
				if (e != null) {
					LOG.error(e.getMessage());
					if (out instanceof ObjectNode){
						try {
							LOG.error("json:\n"+new ObjectMapper().writeValueAsString(out));
						} catch (JsonProcessingException e1) {
						}
					} else {
						LOG.error("message:\n"+out);						
					} 
					
					initErrorHandler();
					eh.handleError(e);	
				} else {
					performStorage(address, data);
				}
				
				return null;
			};
	}

	protected CompletableFuture<ResourceResponse> sendCardResponse(JsonNode json, TeamsAddressable address, Map<String, Object> data, String messageId) throws Exception {		
		Activity out = createActivity(messageId);
		Attachment body = new Attachment();
		body.setContentType("application/vnd.microsoft.card.adaptive");
		body.setContent(json);
		out.getAttachments().add(body);
		return teamsConversations.handleActivity(out, address);
	}

	protected void performStorage(TeamsAddressable address, Map<String, Object> data) {
		String dataKey = (String) data.get(StorageIDResponseHandler.STORAGE_ID_KEY);
		if (dataKey != null) {
			// first, store data for message
			Map<String, String> tags = createStorageTags(data, address);
			String file = address.getKey()+"/"+dataKey;
			teamsState.store(file, tags, data);			
		}
	}
	
	protected Map<String, String> createStorageTags(Map<String, Object> data, TeamsAddressable address) {
		Map<String, String> out = new HashMap<String, String>();
		HeaderDetails h = (HeaderDetails) data.get(HeaderDetails.KEY);
		if (h != null) {
			h.getTags().forEach(t -> out.put(t, TeamsStateStorage.PRESENT));
		}
		
		out.put(TeamsStateStorage.ADDRESSABLE_KEY, address.getKey());
		return out;
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