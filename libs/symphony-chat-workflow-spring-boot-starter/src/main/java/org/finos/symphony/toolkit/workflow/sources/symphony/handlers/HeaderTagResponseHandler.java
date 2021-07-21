package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.HeaderDetails;

public class HeaderTagResponseHandler implements ResponseHandler {

	/**
	 * This ensures that the JSON data being sent will contain a HeaderDetails
	 * object, which contains a list of {@link HashTag}s that need to be present in
	 * the message for indexing purposes.
	 */
	@Override
	public void accept(Response t) {

		if (t instanceof WorkResponse) {
			WorkResponse workResponse = (WorkResponse) t;

			HeaderDetails hd = (HeaderDetails) workResponse.getData().get(HeaderDetails.KEY);
			Object o = workResponse.getData().get(WorkResponse.OBJECT_KEY);
			if (hd == null) {
				hd = new HeaderDetails();
				workResponse.getData().put(HeaderDetails.KEY, hd);
			}

			// make sure all tags are unique, maintain order from original.
			Set<Tag> tags = new LinkedHashSet<>(TagSupport.classHashTags(o));
			tags.addAll(hd.getTags());
			hd.setTags(new ArrayList<Tag>(tags));

		}
	}

	@Override
	public int getOrder() {
		return MEDIUM_PRIORITY;
	}

}
