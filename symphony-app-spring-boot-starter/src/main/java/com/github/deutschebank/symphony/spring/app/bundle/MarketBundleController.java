package com.github.deutschebank.symphony.spring.app.bundle;

import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.github.deutschebank.symphony.spring.app.SymphonyAppProperties;
import com.github.deutschebank.symphony.spring.app.pods.info.PodInfoController;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Constructs the Symphony Bundle File for when you are supplying bundle=<url>
 * to the symphony client in the brows
 * @author robmoffat
 * 
 */
public class MarketBundleController extends AbstractBundleController {

	public MarketBundleController(SymphonyAppProperties p, SymphonyIdentity id, View v) {
		super(p, v, id);
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Map<String, Object> out = new LinkedHashMap<>();
			out.put("type", "AppType");
			out.put("name", p.getName()); 
			out.put("description", p.getDescription());
			out.put("publisher", p.getPublisher());
			out.put("loadUrl", getControllerPageUrl(request));
			out.put("domain", getDomain(request));
			out.put("iconUrl", getAppIconUrl(request));
			out.put("notification", getNotification(request));
			out.put("permissions", p.getPermissions());
			out.put("allowOrigins", getAllowOrigins(request));
			out.put("appGroupId", p.getGroupId());
			addIdentity(out);
			ModelAndView mv = new ModelAndView(v, out);
			return mv;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected void addIdentity(Map<String, Object> out) throws Exception {
		if (appIdentity.getCertificateChain().length == 1) {
			X509Certificate singleCert = appIdentity.getCertificateChain()[0];
			out.put("certificate", singleCert);
		}

		out.put("rsaKey", appIdentity.getPublicKey());
	}

	protected Object getNotification(HttpServletRequest request) throws URISyntaxException {
		Map<String, Object> notification = new HashMap<>();
		notification.put("url", getAppContentUriRoot(request)+PodInfoController.POD_INFO_PATH);
		notification.put("apiKey", p.getApiKey());
		return notification;
	}

	@Override
	public String getPath() {
		return p.getAppPath() + "/" + p.getApiKey() + "/bundle.json";
	}
}
