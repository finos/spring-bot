package org.finos.springbot.workflow.response;

import java.util.Map;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;

/**
 * A Response that contains some JSON data to be included in the message.
 * 
 * @author rob@kite9.com
 *
 */
public class DataResponse implements Response {

	private final Map<String, Object> data;
	private final String templateName;
	private final Addressable to;

	public DataResponse(Addressable to, Map<String, Object> data, String templateName) {
		super();
		this.to = to;
		this.data = data;
		this.templateName = templateName;
	}

	public Map<String, Object> getData() {
		return data;
	}

	@Override
	public String toString() {
		return "DataResponse [data=" + data + ", template=" + templateName + "]";
	}

	/**
	 * An optional template name, which will be used for customizing the formatting of the
	 * response (depending on the output channel).  If the template cannot be resolved 
	 * given the name, the {@link ResponseHandler} will fallback to a default.
	 */
	public String getTemplateName() {
		return templateName;
	}

	@Override
	public Addressable getAddress() {
		return to;
	}

}
