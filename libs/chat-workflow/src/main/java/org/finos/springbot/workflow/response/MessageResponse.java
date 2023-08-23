package org.finos.springbot.workflow.response;

import java.util.HashMap;
import java.util.Map;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Message;

/**
 * A Response that includes a message {@link Content} to send back to the user.
 * 
 * @author rob@kite9.com
 *
 */
public class MessageResponse extends DataResponse {
	
	private final Content m;
	
	public MessageResponse(Addressable stream, Map<String, Object> data, Content m, String templateName) {
		super(stream, data, templateName);
		this.m = m;
	}
	
	public MessageResponse(Addressable stream, String textContent) {
		this(stream, Message.of(textContent));
	}
	

	public MessageResponse(Addressable stream, Content m) {
		super(stream, new HashMap<>(), null);
		this.m = m;
	}
	

	public MessageResponse(Addressable stream, Content m, String template) {
		super(stream, new HashMap<>(), template);
		this.m = m;
	}
	
	
	public Content getMessage() {
		return m;
	}

	@Override
	public String toString() {
		return "MessageResponse [m=" + m + ", getData()=" + getData() + ", getTemplate()=" + getTemplateName()
				+ ", getAddress()=" + getAddress() + "]";
	}

	
}
