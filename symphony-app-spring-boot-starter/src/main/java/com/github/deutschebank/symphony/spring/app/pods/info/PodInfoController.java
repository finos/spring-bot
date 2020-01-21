package com.github.deutschebank.symphony.spring.app.pods.info;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.spring.app.AbstractJsonController;
import com.github.deutschebank.symphony.spring.app.SymphonyAppProperties;
import com.symphony.api.id.SymphonyIdentity;

/**
 * This is invoked when the application is added to the Symphony Market in a pod.
 * A call is made to the notification endpoint to tell the app to register the pod.
 * 
 * When this happens, we add the pod to the {@link PodInfoStore}
 * 
 * @author Rob Moffat
 *
 */
public class PodInfoController extends AbstractJsonController {

	public static final String POD_INFO_PATH = "/podInfo";

	private PodInfoStore store;

	private ObjectMapper om;
	
	public PodInfoController(SymphonyAppProperties p, View v, SymphonyIdentity appIdentity, PodInfoStore store, ObjectMapper om) {
		super(p, v, appIdentity);
		this.store = store;
		this.om = om;
	}
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if ("POST".equals(request.getMethod()) && (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType()))) {
			checkSecretKey(request);
			PodInfo podInfo = (PodInfo) om.readValue(request.getInputStream(), PodInfo.class);
			store.setPodInfo(podInfo);
			ModelAndView model = new ModelAndView(v);
		    model.setStatus(HttpStatus.OK);
		    return model;
		} else {
			throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "This endpoint requires POST of application/json");
		}
	}

	private void checkSecretKey(HttpServletRequest request) {
		String secret = request.getHeader("Secret-Key");
		if (!p.getApiKey().equals(secret)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Secret-Key header doesn't match apiKey");
		}
	}

	@Override
	public String getPath() {
		return p.getAppPath() + POD_INFO_PATH;
	}

}
