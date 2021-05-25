package org.finos.symphony.toolkit.koreai.request;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

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
    private final KoreAIResponseBuilder koreAIResponseBuilder;
    protected Client client;
    protected JsonNodeFactory jnf;
    protected String jwt;
    protected String url;
    
    public KoreAIRequesterImpl(KoreAIResponseHandler koreaiResponseParser, 
    		KoreAIResponseBuilder koreAIResponseBuilder, 
    		String url, 
    		JsonNodeFactory jsonNodeFactory, 
    		String jwt) {
        this.koreaiResponseParser = koreaiResponseParser;
        this.koreAIResponseBuilder = koreAIResponseBuilder;
        this.url = url;
        this.jnf = jsonNodeFactory;
        this.jwt = jwt;
        this.client = createClient();
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
		String out = client.target(url).request()
			.accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + jwt)
			.header("Content-Type", "application/json")
			.post(json)
			.readEntity(String.class);
				
		KoreAIResponse kr = koreAIResponseBuilder.formatResponse(out);
		return kr;
	}
 

	@Override
	public void afterPropertiesSet() throws Exception {
		this.client = createClient();
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
}

   
