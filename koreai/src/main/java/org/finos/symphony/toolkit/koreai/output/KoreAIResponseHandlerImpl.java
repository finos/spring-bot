package org.finos.symphony.toolkit.koreai.output;

import java.io.IOException;

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
    private ResourceLoader rl;
    private ObjectMapper symphonyObjectMapper;
    private String templatePrefix;

    public KoreAIResponseHandlerImpl(MessagesApi messagesApi, 
    		ResourceLoader rl, 
    		boolean skipEmptyAnswers, 
    		ObjectMapper symphonyObjectMapper,
    		String templatePrefix) {
        this.messagesApi = messagesApi;
        this.skipEmptyAnswers = skipEmptyAnswers;
        this.rl = rl;
        this.symphonyObjectMapper = symphonyObjectMapper;
        this.templatePrefix = templatePrefix;
    }

    @Override
	public void handle(Address to, KoreAIResponse koreaResponse) {
    	LOG.info("KoreAIResponseMessageAdapter address={} response={}", to, koreaResponse);
    	
        try {
        	
        	if(skipEmptyAnswers) {
	            if (((String) koreaResponse.getMessageML()).contains("I am unable to find an answer")) {
	            	LOG.warn("Returning nothing");
	            	return;
	            }
        	}
             
            LOG.debug("Prepared Response: {}", koreaResponse);
            
            sendMessage(to, koreaResponse, loadSymphonyTemplate(koreaResponse));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

	private String loadSymphonyTemplate(KoreAIResponse koreaResponse) throws IOException {
		String name = koreaResponse.getSymphonyTemplate();
		Resource r = rl.getResource(templatePrefix+name+".ftl");
		if (r.exists()) {
			return StreamUtils.copyToString(r.getInputStream(), Charsets.UTF_8);
		} else {
			// try for default template
			r = rl.getResource("classpath:/templates/default/koreai-"+name+".ftl");
			if (r.exists()) {
				return StreamUtils.copyToString(r.getInputStream(), Charsets.UTF_8);
			}
		}
		
		throw new RuntimeException("Template not found for: "+name);
	}

    public void sendMessage(Address to, KoreAIResponse koreaResponse, String template) throws Exception {
    	EntityJson out = new EntityJson();
    	out.put("koreai", koreaResponse);
    	String json = symphonyObjectMapper.writeValueAsString(out);
		messagesApi.v4StreamSidMessageCreatePost(null, to.getRoomStreamID(), template, json, null, null, null, null);
	}

	
}
