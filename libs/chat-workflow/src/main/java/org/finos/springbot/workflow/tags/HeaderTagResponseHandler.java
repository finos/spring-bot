package org.finos.springbot.workflow.tags;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;

public class HeaderTagResponseHandler implements ResponseHandler {

	/**
	 * This ensures that the JSON data being sent will contain a HeaderDetails
	 * object, which contains a list of tags used for indexing the data content of the 
	 * {@link Work} in the message.
	 */
	@Override
	public void accept(Response t) {

		if (t instanceof WorkResponse) {
			WorkResponse workResponse = (WorkResponse) t;

			HeaderDetails hd = (HeaderDetails) workResponse.getData().get(HeaderDetails.KEY);
			if (hd == null) {
				hd = new HeaderDetails();
				workResponse.getData().put(HeaderDetails.KEY, hd);
			}

			// make sure all tags are unique, maintain order from original.
			Set<String> tags = new LinkedHashSet<>();
			tags.addAll(hd.getTags());
			
			// check through other stuff in the json response
			for (Object o2 : workResponse.getData().values()) {
				Work w = o2 != null ? o2.getClass().getAnnotation(Work.class) : null;
				if ((w != null) && (w.index())) {
					tags.addAll(TagSupport.classTags(o2));
				}
			}
			
			
			hd.setTags(new ArrayList<String>(tags));

		}
	}

	@Override
	public int getOrder() {
		return MEDIUM_PRIORITY;
	}

}
