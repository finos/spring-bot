package org.finos.symphony.toolkit.koreai.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * https://developer.kore.ai/docs/bots/channel-enablement/adding-webhook-channel/
 *
 * @author rodriva
 */
public class KoreAIRequesterImpl implements KoreAIRequester, InitializingBean {
	
    private static final Logger LOG = LoggerFactory.getLogger(KoreAIRequesterImpl.class);
	
    private final KoreAIResponseHandler koreaiResponseParser;
    protected Client client;
    protected JsonNodeFactory jnf;
    protected String jwt;
    protected String url;
    protected ObjectMapper om;
    
    
	String messageTemplate = "classpath:templates/koreai-message.ftl";
    String formTemplate = "classpath:templates/koreai-form.ftl";

    
    public KoreAIRequesterImpl(KoreAIResponseHandler koreaiResponseParser, String url, JsonNodeFactory jsonNodeFactory, String jwt, ObjectMapper om) {
        this.koreaiResponseParser = koreaiResponseParser;
        this.url = url;
        this.jnf = jsonNodeFactory;
        this.jwt = jwt;
        this.om = om;
    }
    
    protected Client createClient() {
    	return ClientBuilder.newBuilder()
    		.connectTimeout(15, TimeUnit.SECONDS)
    		.readTimeout(15, TimeUnit.SECONDS)
    		.build();
    }

    public void send(Address a, String symphonyQuery) {
    	LOG.info("Request to Kore AI:\n Address={}\n url={}\n Message={} \n jwt={}", a, url, symphonyQuery, jwt);
    	try {
			koreaiResponseParser.handle(a, requestToKoreAI(a, symphonyQuery));
		} catch (Exception e) {
	    	LOG.error("Request to Kore AI FAILED:\n Address={}\n url={}\n Message={}", a, url, symphonyQuery);
	    	LOG.error("Caused by: ", e);
		}
    
    }

	protected KoreAIResponse requestToKoreAI(Address a, String symphonyQuery) throws Exception {
		Entity<Object> json = Entity.json(buildPayload(a, symphonyQuery));
		KoreAIResponse out = client.target(url).request()
			.accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + jwt)
			.header("Content-Type", "application/json")
			.post(json)
			.readEntity(KoreAIResponse.class);
				
		out = postProcessKoreAIResponse(out);
		return out;
	}
    
	/**
	 * Some extra rules to handle weird KoreAI things, and make sure we get back a good-looking response object.
	 */
	protected KoreAIResponse postProcessKoreAIResponse(KoreAIResponse out) throws Exception {
		  // this seems weird - one response wrapped in another?
		Object text = out.getText();
		if ((text instanceof String) && (((String)text).startsWith("{\"text\""))) {
            out = om.readValue((String) out.getText(), KoreAIResponse.class);
        }
        
        // handle formatting for messageML
        handleMessageMLConversion(out);
        
        // handle template choice
        handleOptions(out);
        
        return out;
	}

	public static final String BR = "<br />";
	
	private void handleMessageMLConversion(KoreAIResponse out) {
		String text = out.getText() instanceof String ? 
				(String) out.getText() 
				: ((List<String>) out.getText()).get(0);
		
		// convert newlines to <br />
		text = text.replaceAll("\\\\n", "\n").replaceAll("\n", BR);
		
		// now convert urls to symphony format
		text = text.replaceAll("(https?:\\/\\/[\\w.\\/\\+_\\=\\-\\?]*)", "<a href=\"$1\">$1</a>");
		out.setMessageML(text);
	}
	
	public static final Pattern OPTION = Pattern.compile("^[ ]?[a-z]\\)\\ (.*)$");

	public void handleOptions(KoreAIResponse template) {
		String messageML = template.getMessageML();
		
        String[] multiline = messageML.split(BR);
        StringBuilder text = new StringBuilder();
        List<String> options = new ArrayList<String>();
        for (String string : multiline) {
        	Matcher m = OPTION.matcher(string);
			if (m.find()) {
				options.add(m.group(1));
			} else {
				text.append(string);
				text.append(BR);
			}
		}
        
        template.setOptions(options);
        template.setMessageML(text.toString());
        if (!options.isEmpty()) {
        	template.setForm(formTemplate);
        } else {
        	template.setForm(messageTemplate);
        }
    }

	public Object buildPayload(Address a, String text) {
        LOG.info("buildPayload for text={} address={}", text, a); 

        ObjectNode payload = jnf.objectNode().put("to", "");
        payload.set("session", jnf.objectNode().put("new", false));
        payload.set("message", jnf.objectNode().put("text", text));
        payload.set("from", jnf.objectNode()
        		.put("id", String.valueOf(a.getUserId()))
        		.set("userInfo", jnf.objectNode()
        				.put("firstName", a.getFirstName())
        				.put("lastName", a.getLastName())
        				.put("email", a.getEmail())));
        
        LOG.info("Constructed Payload for KoreAI={}", payload);
        return payload;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		this.client = createClient();
	}
    
    
    
}
