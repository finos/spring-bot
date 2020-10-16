package com.github.deutschebank.symphony.koreai.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.github.deutschebank.symphony.json.ObjectMapperFactory;
import com.github.deutschebank.symphony.koreai.Address;
import com.symphony.api.agent.MessagesApi;

/**
 * This class is responsible for taking the repsonse from KoreAI and processing it.  
 * 
 * @author rodriva
 */
public class KoreaiResponseMessageAdapter implements InitializingBean, KoreAIResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KoreaiResponseMessageAdapter.class);

    private ObjectMapper om;
    private final MessagesApi messagesApi;
    private final String template;

    public KoreaiResponseMessageAdapter(MessagesApi messagesApi, String template) {
        this.messagesApi = messagesApi;
        this.template = template;
    }


    public static final String BR = "<br />\n";

    public String parse(String input) {
        return input
                .replaceAll("\\\\n", "\n")
                .replaceAll("\n", BR)
                .replaceAll("(https?:\\/\\/[\\w.\\/\\+_\\=\\-\\?]*)", "<a href=\"$1\">$1</a>");

    }

    @Override
	public void handle(Address to, KoreAIResponse koreaResponse) {
    	LOG.info("KoreAIResponseMessageAdapter address={} response={}", to, koreaResponse);
    	
        try {
            if (koreaResponse.getText().contains("I am unable to find an answer")) {
            	LOG.warn("Returning nothing");
            	return;
            }

            // this seems weird - one response wrapped in another?
            if (koreaResponse.getText().startsWith("{\"text\"")) {
                koreaResponse = om.readValue(koreaResponse.getText(), KoreAIResponse.class);
            }
            
            String parsed = parse(koreaResponse.getText());
            koreaResponse.setText(parsed);

            if (koreaResponse.isTemplate()) {
            	handleButtons(koreaResponse);
            }
            
            LOG.debug("Prepared Response: {}", koreaResponse);
            
            sendMessage(to, koreaResponse);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void sendMessage(Address to, KoreAIResponse koreaResponse) throws Exception {
    	EntityJson out = new EntityJson();
    	out.put("koreai", koreaResponse);
    	String json = om.writeValueAsString(out);
		messagesApi.v4StreamSidMessageCreatePost(null, to.getRoomStreamID(), template, json, null, null, null, null);
	}

	public void handleButtons(KoreAIResponse template) {
        String[] multiline = template.getText().split(BR);
        List<String> options = Arrays.asList(Arrays.copyOfRange(multiline, 1, multiline.length));
        template.setOptions(options);
        template.setText(multiline[0]);
        template.setForm("koreai-passthrough");
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
