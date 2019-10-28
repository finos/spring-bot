package com.github.deutschebank.symphony.spring.app.bundle;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;

import com.github.deutschebank.symphony.spring.app.AbstractJsonController;
import com.github.deutschebank.symphony.spring.app.SymphonyAppProperties;
import com.symphony.api.id.SymphonyIdentity;

public abstract class AbstractBundleController extends AbstractJsonController {

	public AbstractBundleController(SymphonyAppProperties p, View v, SymphonyIdentity appIdentity) {
		super(p, v, appIdentity);
	}

	protected String getAppIconUrl(HttpServletRequest request) throws URISyntaxException {
		String iconPath = p.getIconPath() == null ? "/icon.png" : p.getIconPath();
		return  getApplicationRoot(request) + iconPath;
	}

	protected String getAllowOrigins(HttpServletRequest request) throws URISyntaxException {
		String applicationRoot = getApplicationRoot(request);
		if (StringUtils.isEmpty(p.getAllowOrigins())) {
			URI u = new URI(applicationRoot);
			return u.getHost();
		} else {
			return p.getAllowOrigins();
		}
	}

	protected String getDomain(HttpServletRequest request) throws URISyntaxException {
		String applicationRoot = getApplicationRoot(request);
		URI u = new URI(applicationRoot);
		String host = u.getHost();
		if (host.contains(".")) {
			return host.substring(host.indexOf("."));
		} else {
			return host;
		}
	}

}