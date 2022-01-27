package org.finos.springbot.symphony.stream.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.finos.springbot.symphony.stream.cluster.ClusterMember;
import org.finos.springbot.symphony.stream.cluster.messages.ClusterMessage;
import org.finos.springbot.symphony.stream.handler.SymphonyStreamHandlerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Minimal controller that allows a web-based spring-boot application to listen for cluster messages 
 * at an endpoint, defined by symphony.stream.endpoint-path.
 *
 * @author robmoffat
 */
public class HttpClusterMessageController implements Controller, ApplicationContextAware {
	
	private View symphonyJsonOutputView;
	private ObjectMapper om;
	private ApplicationContext ctx;
	
	public HttpClusterMessageController(View symphonyJsonOutputView, ObjectMapper om) {
		this.symphonyJsonOutputView = symphonyJsonOutputView;
		this.om = om;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if ("POST".equals(request.getMethod()) && (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType()))) {
			ClusterMessage in = (ClusterMessage) om.readValue(request.getInputStream(), ClusterMessage.class);
			ClusterMember member = getClusterMember(in);
			if (member != null) {
				ClusterMessage out = member.receiveMessage(in);
				Map<String, Object> map = new HashMap<>();
				map.put("response", out);
				ModelAndView r = new ModelAndView(symphonyJsonOutputView, map);
				return r;
			}
		} 
		
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

	/**
	 * This is basically a "lazy wiring" approach so that we don't create a cyclical
	 * dependency over the existence of custer member.
	 * @return
	 */
	private ClusterMember getClusterMember(ClusterMessage in) {
		SymphonyStreamHandlerFactory f = ctx.getBean(SymphonyStreamHandlerFactory.class);
		return f.allClusterMembers().stream()
			.filter(cm -> cm.getClusterName().equals(in.getBotName()))
			.findFirst()
			.orElse(null);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
}
