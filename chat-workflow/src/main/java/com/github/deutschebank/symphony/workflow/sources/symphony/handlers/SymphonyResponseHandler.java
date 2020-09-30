package com.github.deutschebank.symphony.workflow.sources.symphony.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.deutschebank.symphony.workflow.response.AttachmentResponse;
import com.github.deutschebank.symphony.workflow.response.DataResponse;
import com.github.deutschebank.symphony.workflow.response.ErrorResponse;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.MessageResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.SymphonyBot;
import com.github.deutschebank.symphony.workflow.sources.symphony.TagSupport;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.symphony.api.agent.MessagesApi;

/**
 * This is responsible for taking {@link Response} objects and pushing them back to Symphony.
 * 
 * @author Rob Moffat
 *
 */
public class SymphonyResponseHandler implements ResponseHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyResponseHandler.class);

	MessagesApi messagesApi;
	FormMessageMLConverter formConverter;
	EntityJsonConverter jsonConverter;
	SymphonyRooms ru;
	AttachmentHandler ah;
	
	public SymphonyResponseHandler(MessagesApi api, FormMessageMLConverter fmc, EntityJsonConverter ejc, SymphonyRooms ru, AttachmentHandler ah) {
		this.messagesApi = api;
		this.formConverter = fmc;
		this.jsonConverter = ejc;
		this.ru = ru;
		this.ah = ah;
	}
	
	@Override
	public void accept(Response t) {
		if (t instanceof FormResponse) {
			processFormResponse((FormResponse) t);
		} else if (t instanceof ErrorResponse) {
			processErrorResponse((ErrorResponse) t);
		} else if (t instanceof MessageResponse) {
			processMessageResponse((MessageResponse) t);
		} else if (t instanceof AttachmentResponse) {
			processAttachmentResponse((AttachmentResponse) t);
		} else if (t != null) {
			throw new UnsupportedOperationException("Couldn't process response of type "+t.getClass());
		}
	}

	private void processAttachmentResponse(AttachmentResponse t) {
		processDataResponse("", t, ah.formatAttachment(t));
	}

	private void processErrorResponse(MessageResponse t) {
		processDataResponse("!"+t.getMessage(), t, null);
	}

	private void processMessageResponse(MessageResponse t) {
		processDataResponse(t.getMessage(), t, null);
	}

	private void processFormResponse(FormResponse t) {
		String convertedForm = formConverter.convert(t.getFormClass(), t.getFormObject(), t.getButtons(), t.isEditable(), t.getErrors());
		processDataResponse(convertedForm, t, null);
	}


	private void processDataResponse(String messageBody, DataResponse t, Object attachment) {
		String tags = createWorkflowHeader(t);
		String outMessage = "<messageML>"+tags+messageBody+"</messageML>";
		String json = jsonConverter.toWorkflowJson(t.getData());
		String streamId = ru.getStreamFor(t.getAddress());
		LOG.debug(json);
		LOG.debug(outMessage);
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, outMessage, json, null, attachment, null, null);
	}
	
	private String createWorkflowHeader(DataResponse dr) {
		return " <card accent=\"tempo-bg-color--blue\"><header><h3>" + dr.getName() +"</h3></header>" + 
				"<body><p>"+
				dr.getInstructions()+
				TagSupport.toHashTag(dr.getWorkflow())+
				TagSupport.toHashTag("symphony-workflow")+
				TagSupport.classHashTags(dr.getData()) + 
				"</p></body>" +
				"  </card>";
	}

}
