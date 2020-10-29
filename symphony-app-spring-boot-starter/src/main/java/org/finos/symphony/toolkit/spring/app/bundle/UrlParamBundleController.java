package org.finos.symphony.toolkit.spring.app.bundle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.symphony.api.id.SymphonyIdentity;

/**
 * Constructs the Symphony Bundle File for when you are supplying
 * ?bundle=url to the symphony client in the browser.
 * 
 * Does not contain the app secret.
 * @author rob moffat
 */
public class UrlParamBundleController extends AbstractBundleController {
	
	public UrlParamBundleController(SymphonyAppProperties p, View v, SymphonyIdentity appIdentity) {	
		super(p, v, appIdentity);
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Map<String, Object> out = new LinkedHashMap<>();
			out.put("type", "sandbox");
			out.put("id", appIdentity.getCommonName());
			out.put("name", p.getName()+" (Local)");
			out.put("blurb", p.getDescription());
			out.put("publisher", p.getPublisher());
			out.put("url", getControllerPageUrl(request));
			out.put("domain", getDomain(request));
			out.put("icon", getAppIconUrl(request));
			Map<String, Object> wrapper = new HashMap<>();
			wrapper.put("applications", new Object[] { out });
			ModelAndView mv = new ModelAndView(v, wrapper);
			return  mv;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public String getPath() {
		return p.getAppPath() + "/bundle.json";
	}
}
