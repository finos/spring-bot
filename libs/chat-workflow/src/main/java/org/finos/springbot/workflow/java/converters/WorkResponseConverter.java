package org.finos.springbot.workflow.java.converters;

import java.util.Map;

import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

public class WorkResponseConverter extends AbstractResponseConverter {

	public WorkResponseConverter(ResponseHandlers rh) {
		super(rh);
	}

	@Override
	public void accept(Object t, ChatHandlerExecutor u) {
		if (canConvert(t)) {
			rh.accept(convert(t, u));
		}
	}

	public Response convert(Object source, ChatHandlerExecutor creator) {
		Addressable a = creator.action().getAddressable();
		ChatResponseBody wr = creator.getOriginatingMapping().getHandlerMethod().getMethodAnnotation(ChatResponseBody.class);
		WorkMode wm = WorkMode.VIEW;
		
		if (wr != null) {
			String template = wr.template();
			WorkMode wmAnnotation = wr.workMode();
			
			if (wmAnnotation == WorkMode.EDIT) {
				wm = wmAnnotation;
			}
			
			if (StringUtils.hasText(template)) {
				Map<String, Object> entityMap = WorkResponse.createEntityMap(source, null, null);
				return new WorkResponse(a, entityMap, template, wm, source.getClass());
			} 
		}
			
		return new WorkResponse(a, source, wm, null, null);
	}

	public boolean canConvert(Object in) {
		if (in == null) {
			return false;
		}
		
		Class<?> c = in.getClass();
		Work work = c.getAnnotation(Work.class);
		if (work != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	
}
