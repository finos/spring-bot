package org.finos.symphony.toolkit.koreai.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.koreai.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;

/**
 * This class is responsible for taking the repsonse from KoreAI and processing it.  
 * 
 * @author rodriva
 */
public class KoreAIResponseHandlerImpl implements InitializingBean, KoreAIResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KoreAIResponseHandlerImpl.class);

    private ObjectMapper om;
    private final MessagesApi messagesApi;
    private final boolean skipEmptyAnswers;
    private ResourceLoader rl;

    public KoreAIResponseHandlerImpl(MessagesApi messagesApi, ResourceLoader rl, ObjectMapper symphonyEntityMapper, boolean skipEmptyAnswers) {
        this.messagesApi = messagesApi;
        this.skipEmptyAnswers = skipEmptyAnswers;
        this.rl = rl;
        this.om = symphonyEntityMapper;
    }

    @Override
	public void handle(Address to, KoreAIResponse koreaResponse) {
    	LOG.info("KoreAIResponseMessageAdapter address={} response={}", to, koreaResponse);
    	
        try {
        	
        	if(skipEmptyAnswers) {
	            if (koreaResponse.getText().contains("I am unable to find an answer")) {
	            	LOG.warn("Returning nothing");
	            	return;
	            }
        	}
             
            LOG.debug("Prepared Response: {}", koreaResponse);
            
            sendMessage(to, koreaResponse, 
            	StreamUtils.copyToString(rl.getResource(koreaResponse.getForm()).getInputStream(),
            			Charsets.UTF_8));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void sendMessage(Address to, KoreAIResponse koreaResponse, String template) throws Exception {
    	EntityJson out = new EntityJson();
    	out.put("koreai", koreaResponse);
    	String json = om.writeValueAsString(out);
		messagesApi.v4StreamSidMessageCreatePost(null, to.getRoomStreamID(), template, json, null, null, null, null);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		List<Class<?>> extendedClassSpace = new ArrayList<Class<?>>();
		extendedClassSpace.add(KoreAIResponse.class);
		VersionSpace[] vs = extendedClassSpace.stream().map(c -> new VersionSpace(c.getCanonicalName(), "1.0")).toArray(s -> new VersionSpace[s]);
		om = ObjectMapperFactory.initialize(new ObjectMapper(), ObjectMapperFactory.extendedSymphonyVersionSpace(vs));		
		om.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	
}
