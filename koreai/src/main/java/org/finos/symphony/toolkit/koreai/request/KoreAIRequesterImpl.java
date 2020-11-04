package org.finos.symphony.toolkit.koreai.request;

import java.util.Arrays;
import java.util.List;

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
    	return ClientBuilder.newClient();
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
		KoreAIResponse out =  client.target(url).request()
			.accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + jwt)
			.post(Entity.json(buildPayload(a, symphonyQuery)))
			.readEntity(KoreAIResponse.class);
		
		out = postProcessKoreAIResponse(out);
		return out;
	}
    
	/**
	 * Some extra rules to handle weird KoreAI things, and make sure we get back a good-looking response object.
	 */
	protected KoreAIResponse postProcessKoreAIResponse(KoreAIResponse out) throws Exception {
		  // this seems weird - one response wrapped in another?
        if (out.getText().startsWith("{\"text\"")) {
            out = om.readValue(out.getText(), KoreAIResponse.class);
        }
        
        // handle formatting for messageML
        handleMessageMLConversion(out);
        
        // handle template choice
        if (out.isTemplate()) {
        	handleForm(out);
        } else {
        	handleMessage(out);
        }
        
        return out;
	}

	private void handleMessage(KoreAIResponse out) {
		out.setForm(messageTemplate);
	}

	public static final String BR = "<br />";
	
	private void handleMessageMLConversion(KoreAIResponse out) {
		// convert newlines to <br />
		String txt = out.getText().replaceAll("\\\\n", "\n").replaceAll("\n", BR);
		
		// now convert urls to symphony format
		txt = txt.replaceAll("(https?:\\/\\/[\\w.\\/\\+_\\=\\-\\?]*)", "<a href=\"$1\">$1</a>");
		out.setText(txt);
	}

	public void handleForm(KoreAIResponse template) {
        String[] multiline = template.getText().split(BR);
        List<String> options = Arrays.asList(Arrays.copyOfRange(multiline, 1, multiline.length));
        template.setOptions(options);
        template.setText(multiline[0]);
		template.setForm(formTemplate);
    }

	public Object buildPayload(Address a, String text) {
        LOG.info("buildPayload for text={} address={}", text, a); 

        ObjectNode payload = jnf.objectNode().put("to", "");
        payload.set("session", jnf.objectNode().put("new", "false"));
        payload.set("message", jnf.objectNode().put("text", text));
        payload.set("from", jnf.objectNode()
        		.put("id", String.valueOf(a.getUserId()))
        		.set("userInfo", jnf.objectNode()
        				.put("firstName", a.getFirstName())
        				.put("lastName", a.getLastName())
        				.put("email", a.getEmail())));
        
        LOG.info("Constructed Payload for KoreAI={}", payload);
        return payload.toString();
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		this.client = createClient();
	}
    
    
    
}
