package org.finos.symphony.toolkit.spring.app.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.finos.symphony.toolkit.spring.app.AbstractJsonController;
import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties.CircleOfTrust;
import org.finos.symphony.toolkit.spring.app.tokens.app.AppTokenStrategy;
import org.finos.symphony.toolkit.spring.app.tokens.pod.PodTokenStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.ExtensionAppTokens;

/**
 * This handles the circle-of-trust concerns of the application, by
 * connecting to a given symphony pod, and getting back an authentication token (Ta) and generating
 * a token for the client (Ts).  
 * 
 * See: https://developers.symphony.com/extension/docs/application-authentication
 * 
 * @author Rob Moffat
 *
 */
public class PodAuthController extends AbstractJsonController {
	
	public static final Logger LOG = LoggerFactory.getLogger(PodAuthController.class);
	
	public static final String POD_AUTH_PATH = "/podAuth";

	private List<PodTokenStrategy> podTokenStrategies;
	
	private AppTokenStrategy appTokenStrategy;
	
	private CircleOfTrust circleOfTrust;
	
	public PodAuthController(SymphonyAppProperties p, View v, SymphonyIdentity appIdentity, AppTokenStrategy appTokenStrategy, List<PodTokenStrategy> tokenStrategies) {
		super(p, v, appIdentity);
		this.podTokenStrategies = tokenStrategies;
		this.appTokenStrategy = appTokenStrategy;
		this.circleOfTrust = p.getCircleOfTrust();
	}
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String qs = request.getQueryString();
		Map<String, String> params = decodeQuery(qs);
		Map<String, Object> appData = new HashMap<>();
		appData.put("circleOfTrust", circleOfTrust);
		appData.put("appId", appIdentity.getCommonName());
		
		if ((params.containsKey("podId")) && (this.circleOfTrust != CircleOfTrust.OFF)) {
			
			// do circle of trust if pod id is set.
			String podId = params.get("podId");
			String appToken = appTokenStrategy.generateAppToken();
			ExtensionAppTokens tokens = queryStrategies(podId, appToken);
			appTokenStrategy.storeAppToken(appToken, tokens.getSymphonyToken());
			appData.put("tokenA", tokens.getAppToken());
			appData.put("appId", tokens.getAppId());
			appData.put("expireAt", tokens.getExpireAt());	
		}
		
		ModelAndView mv = new ModelAndView(v, appData);
		return mv;
	}

	public ExtensionAppTokens queryStrategies(String podId, String appToken) {
		for (PodTokenStrategy podTokenStrategy : podTokenStrategies) {
			ExtensionAppTokens tokens = null;
			try {
				tokens = podTokenStrategy.getTokens(appToken, podId);
			} catch (Exception e) {
				LOG.warn("Received error getting pod tokens", e);
			}
			
			if (tokens != null) {
				return tokens;
			}
		}
		
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Couldn't connect to back-end pod for token");
	}


	@Override
	public String getPath() {
		return p.getAppPath() + POD_AUTH_PATH;
	}

}
