package org.finos.symphony.toolkit.stream.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	private ObjectMapper om;
	
	public HttpClusterMessageController(View symphonyJsonOutputView, ClusterMember cm, ObjectMapper om) {
		this.clusterMember = cm;
		this.symphonyJsonOutputView = symphonyJsonOutputView;
		this.om = om;
	}

	public ClusterMessage receiveClusterMessage(@RequestBody ClusterMessage message) {
		return clusterMember.receiveMessage(message);	
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if ("POST".equals(request.getMethod()) && (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType()))) {
			ClusterMessage in = (ClusterMessage) om.readValue(request.getInputStream(), ClusterMessage.class);
			ClusterMessage out = clusterMember.receiveMessage(in);
			Map<String, Object> map = new HashMap<>();
			map.put("response", out);
			ModelAndView r = new ModelAndView(symphonyJsonOutputView, map);
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
	}
}
