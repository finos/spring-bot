package com.github.deutschebank.symphony.koreai.request;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.deutschebank.symphony.koreai.Address;
import com.github.deutschebank.symphony.koreai.response.KoreAIResponse;
import com.github.deutschebank.symphony.koreai.response.KoreAIResponseHandler;


/**
 * https://developer.kore.ai/docs/bots/channel-enablement/adding-webhook-channel/
 *
 * @author rodriva
 */
public class KoreAIRequesterImpl implements KoreAIRequester, InitializingBean {
	
    private static final Logger LOG = LoggerFactory.getLogger(KoreAIRequesterImpl.class);
	
    private final KoreAIResponseHandler koreaiResponseParser;
    private Client client;
    private JsonNodeFactory jnf;
    private String jwt;
    private String url;

    public KoreAIRequesterImpl(KoreAIResponseHandler koreaiResponseParser, String url, JsonNodeFactory jsonNodeFactory, String jwt) {
        this.koreaiResponseParser = koreaiResponseParser;
        this.url = url;
        this.jnf = jsonNodeFactory;
        this.jwt = jwt;
    }
    
    protected Client createClient() {
    	return ClientBuilder.newClient();
    }

    public void send(Address a, String symphonyQuery) {
    	LOG.info("Request to Kore AI:\n Address={}\n url={}\n Message={} \n jwt={}", a, url, symphonyQuery, jwt);
    	try {
			koreaiResponseParser.handle(a, 
				client.target(url).request()
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwt)
					.get()
					//.post(Entity.json(buildPayload(a, symphonyQuery)))
					.readEntity(KoreAIResponse.class));
		} catch (Exception e) {
	    	LOG.error("Request to Kore AI FAILED:\n Address={}\n url={}\n Message={}", a, url, symphonyQuery);
	    	LOG.error("Caused by: ", e);
		}
    
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
