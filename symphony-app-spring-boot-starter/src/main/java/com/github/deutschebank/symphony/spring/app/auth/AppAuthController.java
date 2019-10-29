package com.github.deutschebank.symphony.spring.app.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.github.deutschebank.symphony.spring.app.AbstractJsonController;
import com.github.deutschebank.symphony.spring.app.SymphonyAppProperties;
import com.github.deutschebank.symphony.spring.app.SymphonyAppProperties.CircleOfTrust;
import com.github.deutschebank.symphony.spring.app.tokens.app.AppTokenStrategy;
import com.symphony.api.id.SymphonyIdentity;

/**
 * This endpoint is called by your front-end to validate that it has the correct symphony tokens.
 * 
 * @author Rob Moffat
 *
 */
public class AppAuthController extends AbstractJsonController {
	
	public static final String APP_AUTH_PATH = "/appAuth";


	private AppTokenStrategy strategy;
	private CircleOfTrust circleOfTrust;
	
	public AppAuthController(SymphonyAppProperties appProperties, View v, SymphonyIdentity appId,
			AppTokenStrategy appTokenStrategy) {
		super(appProperties, v , appId);
		this.strategy = appTokenStrategy;
		this.circleOfTrust = appProperties.getCircleOfTrust();
	}

	@Override
	public String getPath() {
		return p.getAppPath() + APP_AUTH_PATH;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String qs = request.getQueryString();
		Map<String, String> params = decodeQuery(qs);
		String appToken = params.get("appToken");
		String podToken = params.get("podToken");
		
		if (strategy.checkTokens(appToken, podToken)) {
			ModelAndView out = new ModelAndView();
			out.setStatus(HttpStatus.OK);
			out.setView(v);
			out.addObject("circleOfTrust", circleOfTrust);
			out.addObject("appToken", appToken);
			out.addObject("podToken", podToken);
			return out;
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "appToken and podToken don't match");	
		}
	}

}
