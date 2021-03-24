package org.finos.symphony.toolkit.spring.app.jwt;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.finos.symphony.toolkit.spring.app.AbstractJsonController;
import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Reports back details of the user, given the JWT token supplied (which should be signed by Symphony).
 * 
 * This is supplied for testing purposes, or to suggest how to develop similar controllers.
 * 
 * @author Rob Moffat
 */
public class UserDetailsController extends AbstractJsonController {
	
	public static final String USER_DETAILS_PATH = "/userDetails";

	public UserDetailsController(SymphonyAppProperties p, View v, SymphonyIdentity id, ObjectMapper objectMapper) {
		super(p, v, id);
	}

	@Override
	public String getPath() {
		return p.getAppPath() + USER_DETAILS_PATH;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		
		if (a instanceof OAuth2Authentication) {
			OAuth2Authentication aa = (OAuth2Authentication) a;
			Map<String, Object> out = new HashMap<>();
			out.put("principal", aa.getName());
			out.put("details", aa.getOAuth2Request().getRequestParameters());
			ModelAndView r = new ModelAndView(v, out);
		return r;
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user details set");
		}
	}


}
