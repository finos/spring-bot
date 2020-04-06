package com.github.deutschebank.symphony.stream.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import com.github.deutschebank.symphony.stream.cluster.ClusterMember;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Minimal controller that allows a web-based spring-boot application to listen for cluster messages 
 * at an endpoint, defined by symphony.stream.endpoint-path.
 *
 * @author robmoffat
 */
public class HttpClusterMessageController implements Controller {
	
	private ClusterMember clusterMember;
	private View symphonyJsonOutputView;
	
	public HttpClusterMessageController(View symphonyJsonOutputView, ClusterMember cm) {
		this.clusterMember = cm;
		this.symphonyJsonOutputView = symphonyJsonOutputView;
	}

	public ClusterMessage receiveClusterMessage(@RequestBody ClusterMessage message) {
		return clusterMember.receiveMessage(message);	
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClusterMessage in = null;
		ClusterMessage out = clusterMember.receiveMessage(in);
		Map<String, Object> map = new HashMap<>();
		map.put("response", out);
		ModelAndView r = new ModelAndView(symphonyJsonOutputView, map);
		return r;
		
	}
}
