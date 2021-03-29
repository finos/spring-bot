package org.finos.symphony.toolkit.spring.app.controller;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.finos.symphony.toolkit.spring.app.auth.AppAuthController;
import org.finos.symphony.toolkit.spring.app.auth.PodAuthController;
import org.springframework.util.StringUtils;

/**
 * Handles the output of the controller.html page.
 * 
 * Supply modules that need to be imported into the controller page to handle
 * your app's functionality.
 * 
 * @author Rob Moffat
 */
public class ControllerPageController extends ThymeleafPageController {

	public ControllerPageController(SymphonyAppProperties appProperties) {
		super(appProperties, appProperties.getAppPath() + getControllerPath(appProperties), "symphony-app/controller",
				Collections.emptyMap());
	}

	protected Map<String, Object> buildModel(HttpServletRequest request) throws Exception {
		Map<String, Object> out = super.buildModel(request);
		String qs = request.getQueryString();
		Map<String, String> qp = decodeQuery(qs);
		boolean devMode = !StringUtils.isEmpty(qp.get("dev"));

		out.put("podAuthUrl", getPodAuthUrl(request, devMode));
		out.put("appAuthUrl", getAppAuthUrl(request, devMode));
		return out;
	}

	protected String getPodAuthUrl(HttpServletRequest request, boolean devMode) throws URISyntaxException {
		String appRoot = getApplicationRoot(request);
		return appRoot + p.getAppPath() + PodAuthController.POD_AUTH_PATH + (devMode ? "?dev=true&podId=" : "?podId=");
	}

	protected String getAppAuthUrl(HttpServletRequest request, boolean devMode) throws URISyntaxException {
		String appRoot = getApplicationRoot(request);
		return appRoot + p.getAppPath() + AppAuthController.APP_AUTH_PATH + (devMode ? "?dev=true&" : "?");
	}

}
