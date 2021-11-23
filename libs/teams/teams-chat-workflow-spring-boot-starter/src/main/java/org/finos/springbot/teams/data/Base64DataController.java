package org.finos.springbot.teams.data;

import java.util.Base64;
import java.util.Map;

import org.finos.springbot.teams.TeamsException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides the back-end of the URL, allowing users to decode the JSON payload from a 
 * link for a given chat message.  Not necessary but helpful for debugging.
 * 
 * @author rob@kite9.com
 *
 */
@Controller
public class Base64DataController {

	public static final String DATA_TRANSPORT_PATH="b64data/";
	
	private final ObjectMapper om;

	public Base64DataController(ObjectMapper attachmentDataMapper) {
		this.om = attachmentDataMapper;
	}
	
	@GetMapping(
			produces = MediaType.APPLICATION_JSON_VALUE, 
			path = "/"+DATA_TRANSPORT_PATH+"/{base64str}")
	@ResponseBody
	public Object decode(@PathVariable(name = "base64str") String base64str) {
		try {
			TypeReference<Map<String, Object>> tr = new TypeReference<Map<String,Object>>() {};
			return om.readValue(Base64.getDecoder().decode(base64str), tr);
		} catch (Exception e) {
			throw new TeamsException("Couldn't decode base64", e);
		}
	}
	
}
