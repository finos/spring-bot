package org.finos.symphony.webhookbot.domain;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Work(editable = true, instructions = "", name = "WebHook Definition")
public class WebHook {

	private HashTag hookId;
	private HashTag hashTag;
	private String displayName;
	private String url;
	private List<Filter> filters = new ArrayList<Filter>();
	private String template = "";
	private boolean active = true;
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public HashTag getHashTag() {
		return hashTag;
	}
	
	public void setHashTag(HashTag hashTag) {
		this.hashTag = hashTag;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<Filter> getFilters() {
		return filters;
	}
	
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public HashTag getHookId() {
		return hookId;
	}
	
	public void setHookId(HashTag hookId) {
		this.hookId = hookId;
	}

//	@Exposed(description = "Provide a download of the template used by the webhook")
//	public Response template(Workflow wf, Addressable a, ObjectMapper om) {
//		if (!StringUtils.hasText(template)) {
//			return new MessageResponse(wf, a, null, getDisplayName(),"", "No template set for webhook.  Please upload one");
//		} else {
//			byte[] contents = 
//			
//			return new AttachmentResponse(wf, a, null, getDisplayName(), template, null, ".ml"{)
//		}
//	}
	
}
