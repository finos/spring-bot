package org.finos.symphony.toolkit.koreai.output;

import org.apache.commons.codec.Charsets;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.symphony.api.agent.MessagesApi;

/**
 * This class is responsible for taking the response from KoreAI and sending it to Symphony, 
 * with the appropriate messageML template. 
 * 
 * @author rodriva
 */
public class KoreAIResponseHandlerImpl implements KoreAIResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KoreAIResponseHandlerImpl.class);

    private final MessagesApi messagesApi;
    private final boolean skipEmptyAnswers;
    private final boolean sendErrorsToSymphony;
    private ResourceLoader rl;
    private ObjectMapper symphonyObjectMapper;
    private String templatePrefix;

    public KoreAIResponseHandlerImpl(MessagesApi messagesApi, 
    		ResourceLoader rl, 
    		boolean skipEmptyAnswers, 
    		boolean sendErrorsToSymphony,
    		ObjectMapper symphonyObjectMapper,
    		String templatePrefix) {
        this.messagesApi = messagesApi;
        this.skipEmptyAnswers = skipEmptyAnswers;
        this.sendErrorsToSymphony = sendErrorsToSymphony;
        this.rl = rl;
        this.symphonyObjectMapper = symphonyObjectMapper;
        this.templatePrefix = templatePrefix;
    }

    @Override
	public void handle(Address to, KoreAIResponse koreaResponse) {
    	LOG.info("KoreAIResponseMessageAdapter address={} response={}", to, koreaResponse);
    	
    	koreaResponse.getProcessed().stream()
    		.filter(on -> !canSkip(on))
    		.forEach(on -> sendMessage(to, on, loadSymphonyTemplate(on)));
 
    }

	private boolean canSkip(ObjectNode on) {
    	if(skipEmptyAnswers) {
    		TextNode messageML = (TextNode) on.get("messageML");
    		if ((messageML != null) &&(messageML.asText().contains("I am unable to find an answer"))) {
            	return true;
            }
    	}
    	
    	return false;
    }
	
	private String loadSymphonyTemplate(ObjectNode on) {
		String name = on.get(KoreAIResponse.TEMPLATE_TYPE).asText();
		Resource r = rl.getResource(templatePrefix+"/"+name+".ftl");
		if (r.exists()) {
			return loadResourceOrShowError(r);
		} else {
			// try for default template
			r = rl.getResource("classpath:/koreai/templates/default/"+name+".ftl");
			if (r.exists()) {
				return loadResourceOrShowError(r);
			}
		}
		
		return "<messageML><div>No template for: "+name+"</div></messageML>";
	}

	private String loadResourceOrShowError(Resource r) {
		try {
			return StreamUtils.copyToString(r.getInputStream(), Charsets.UTF_8);
		} catch (Exception e) {
			LOG.error("Problem loading template:", e);
			return "<messageML><div>Couldn't load template: "+e.getMessage()+"</div></messageML>";
		}
	}

    public void sendMessage(Address to, ObjectNode on, String template) {
    	EntityJson out = new EntityJson();
    	out.put("koreai", on);
    	String json;
		try {
			json = symphonyObjectMapper.writeValueAsString(out);
			messagesApi.v4StreamSidMessageCreatePost(null, to.getRoomStreamID(), template, json, null, null, null, null);
		} catch (Exception e) {
			if (sendErrorsToSymphony) {
				sendErrorToSymphony(e, to.getRoomStreamID());
			}
			throw new RuntimeException("Couldn't prepare JSON", e);
		}
		
	}

	private void sendErrorToSymphony(Exception e, String roomStreamID) {
		String error = "<messageML><p>KoreAI Bridge Processing Error:</p><code>"+e.getMessage()+"</code></messageML>";
		messagesApi.v4StreamSidMessageCreatePost(null, roomStreamID, error, null, null, null, null, null);
	}

	
}
