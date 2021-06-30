package org.finos.symphony.toolkit.workflow.java.converters;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;

public class FormResponseConverter implements ResponseConverter {

	@Override
	public Response convert(Object source, ChatHandlerExecutor creator) {
		Addressable a = creator.action().getAddressable();
		Work work = source.getClass().getAnnotation(Work.class);
		EntityJson json = new EntityJson();
		json.put(EntityJsonConverter.WORKFLOW_001, source);
		
		return new FormResponse(a, json, null, source.getClass(), false, null)
	}

	@Override
	public boolean canConvert(Object in) {
		Class<?> c = in.getClass();
		Work work = c.getAnnotation(Work.class);
		if (work != null) {
			return true;
		}
		
		return false;
	}

}
