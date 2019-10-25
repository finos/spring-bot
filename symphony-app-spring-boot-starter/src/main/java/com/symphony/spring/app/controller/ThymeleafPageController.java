package com.symphony.spring.app.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.symphony.spring.app.AbstractController;
import com.symphony.spring.app.SymphonyAppProperties;

public class ThymeleafPageController extends AbstractController {
	
	private String path;
	private String template;
	private Map<String, Object> args;

	public ThymeleafPageController(SymphonyAppProperties p, String path, String template, Map<String, Object> args) {
		super(p);
		this.path = path;
		this.template = template;
		this.args = args;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> hm = buildModel(request);
		ModelAndView out = new ModelAndView(template, hm);
		return out;
	}

	protected Map<String, Object> buildModel(HttpServletRequest request) throws Exception {
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("args", args);
		hm.put("applicationRoot", getApplicationRoot(request));
		return hm;
	}

}
