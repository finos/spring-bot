package org.finos.symphony.rssbot.notify;

import org.finos.symphony.rssbot.RSSProperties;
import org.finos.symphony.rssbot.feed.SubscribeRequest;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.symphony.api.agent.MessagesApi;

public class Notifier implements InitializingBean {

	public static final Logger LOG = LoggerFactory.getLogger(Notifier.class);

	
	@Autowired
	MessagesApi api;
	
	@Autowired
	RSSProperties properties;
	
	@Autowired
	ResponseHandler rh;
	
	@Autowired
	Workflow wf;
	
	private RoomDef observationRoom;
	
	
	public void sendSuccessNotification(SubscribeRequest sr, Addressable a, Author author) {
		rh.accept(new MessageResponse(wf, a, new EntityJson(), "RSS Bot", "Feed Created", properties.getSuccessMessage()));
		
		if (observationRoom != null) {			
			rh.accept(new MessageResponse(wf, observationRoom, new EntityJson(), "New Feed Created", "by "+author.getName(), 
				"<ul><li>Url: "+HtmlUtils.htmlEscape(sr.getUrl())+"</li>"+
						"<li>Room: "+a.toString()+"</li></ul>"));
		}
		
	}
	
	public void sendFailureNotification(SubscribeRequest sr, Addressable a, Exception e, Author author) {
		rh.accept(new MessageResponse(wf, a, new EntityJson(), "RSS Bot", "Feed Creation Failed", properties.getFailureMessage()));
		
		if (observationRoom != null) {			
			rh.accept(new MessageResponse(wf, observationRoom, new EntityJson(), "New Feed Creation Failed", "by "+author.getName(), 
				"<ul><li>Url: "+HtmlUtils.htmlEscape(sr.getUrl())+"</li>"+
						"<li>Room: "+a.toString()+"</li>"+
						"<li>"+e.getMessage()+"</li></ul>"));
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (properties.getObservationStreamId() != null) {
			observationRoom = new RoomDef("Observation", "RSS Bot Observations", false, properties.getObservationStreamId());
			LOG.info("Observation room set up: "+observationRoom);
		} else {
			LOG.warn("No observation room configured");
		}
	}

}
