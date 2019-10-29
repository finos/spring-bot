package com.github.deutschebank.symphony.spring.app;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public abstract class AbstractController implements SymphonyController {

	protected SymphonyAppProperties p;

	public AbstractController(SymphonyAppProperties p) {
		this.p = p;
	}

	public String getAppContentUriRoot(HttpServletRequest request) throws URISyntaxException {
		return getApplicationRoot(request)+p.getAppPath();
	}
	
	protected static String getControllerPath(SymphonyAppProperties appProperties) {
		if (appProperties.getControllerPath() == null) {
			String appGroupId = appProperties.getGroupId();
			return "/"+appGroupId+"-controller.html";
		} else {
			return appProperties.getControllerPath();
		}
	}

	protected String getControllerPageUrl(HttpServletRequest request) throws URISyntaxException {
		return getAppContentUriRoot(request)+getControllerPath(p);
	}

	/**
	 * Figures out the application root from the request, or 
	 * uses the app url if one is set (useful behind load balancers).
	 * @throws URISyntaxException 
	 */
	protected String getApplicationRoot(HttpServletRequest request) throws URISyntaxException {
		if (!StringUtils.isEmpty(p.getBaseUrl())) {
			return p.getBaseUrl();
		} else {
			String endpoint = getPath();
			URI uri = new URI(request.getRequestURL().toString());
			String uriStr = uri.toString();
			int from = uriStr.indexOf(endpoint);
			if (from == -1) {
				return uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort();
			} else {
				return uriStr.substring(0, from);
			}
		}
	}

	public Map<String, String> decodeQuery(String qs) {
		if (StringUtils.isEmpty(qs)) {
			return Collections.emptyMap();
		}
	
		Map<String, String> decodedQuery = Arrays.stream(qs.split("&"))
				.map(p -> p.split("="))
				.collect(Collectors.toMap(param -> (String) param[0], param -> decodeParams(param)));
		return decodedQuery;
	}

	protected String decodeParams(String[] param) {
		try {
			return URLDecoder.decode(param[1], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Couldn't decode query string", e);
		}
	}

}