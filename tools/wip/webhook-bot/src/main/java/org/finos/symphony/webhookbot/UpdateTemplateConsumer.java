package org.finos.symphony.webhookbot;

import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.AbstractElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.webhookbot.domain.ActiveWebHooks;
import org.finos.symphony.webhookbot.domain.Template;
import org.finos.symphony.webhookbot.domain.WebHook;
import org.finos.symphony.webhookbot.domain.WebHookOps;
import org.finos.symphony.webhookbot.domain.WebhookPayload;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UpdateTemplateConsumer extends AbstractElementsConsumer {

	@Override
	public List<Response> apply(ElementsAction t) {
		if (t.getAction().equals(WebHookOps.TEMPLATE_UPDATE)) {
			try {
				Template template = (Template) t.getFormData();
				EntityJson workflow = t.getData();
				ActiveWebHooks awh = (ActiveWebHooks) workflow.get(EntityJsonConverter.WORKFLOW_001);
				WebHook active = (WebHook) workflow.get(ReceiveController.INVOKED_WEBHOOK);
				WebHook wh = ReceiveController.getHook(awh, active.getHookId().getId());
				wh.setTemplate(template);
				WebhookPayload payload = (WebhookPayload) workflow.get(ReceiveController.PAYLOAD);
				EntityJson out = ReceiveController.createEntityJson(payload.getContents(), awh, wh);
				MessageResponse mr = new MessageResponse(t.getWorkflow(), t.getAddressable(), out, "" , "", template.getContents());
				return Collections.singletonList(mr);
			} catch (JsonProcessingException e) {
				return Collections.singletonList(new ErrorResponse(t.getWorkflow(), t.getAddressable(), e.getMessage()));
			}
			
				
		} else {
			return Collections.emptyList();
		}
	}

}
