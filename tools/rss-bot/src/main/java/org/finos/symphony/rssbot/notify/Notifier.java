package org.finos.symphony.rssbot.notify;

import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.ErrorResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.rssbot.RSSProperties;
import org.finos.symphony.rssbot.feed.SubscribeRequest;
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
	ResponseHandlers rh;

	private Chat observationRoom;
	
	
	public void sendSuccessNotification(SubscribeRequest sr, Addressable a, User author) {
		rh.accept(new MessageResponse(a, "RSS Bot: Feed Created.  "+properties.getSuccessMessage()));
		
		if (observationRoom != null) {			
			rh.accept(new MessageResponse(observationRoom, "RSS Bot: Feed Created by "+author.getName()+
				" Url: "+HtmlUtils.htmlEscape(sr.getUrl()) +
				" Room: "+a.toString()));
		}
		
	}
	
	public void sendFailureNotification(SubscribeRequest sr, Addressable a, Exception e, User author) {
		rh.accept(new MessageResponse(a, "RSS Bot: Feed Creation Failed. "+ properties.getFailureMessage()));
		rh.accept(new ErrorResponse(a, e));
		
		if (observationRoom != null) {			
			rh.accept(new MessageResponse(observationRoom,  "RSS Bot: New Feed Creation Failed by "+author.getName()+
				" Url: "+HtmlUtils.htmlEscape(sr.getUrl())+
				" Room: "+a.toString()));
			rh.accept(new ErrorResponse(observationRoom, e));
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (properties.getObservationStreamId() != null) {
			observationRoom = new SymphonyRoom("RSS Bot Observations", properties.getObservationStreamId());
			LOG.info("Observation room set up: "+observationRoom);
		} else {
			LOG.warn("No observation room configured");
		}
	}

}
