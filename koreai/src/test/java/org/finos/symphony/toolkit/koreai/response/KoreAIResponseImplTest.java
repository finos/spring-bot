package org.finos.symphony.toolkit.koreai.response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseHandlerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;

/**
 * @author rodriva
 */
@RunWith(SpringRunner.class)
public class KoreAIResponseImplTest {

    private KoreAIResponseHandler parser;
    
    @MockBean
    MessagesApi api;
    
    @Autowired
    ResourceLoader rl;
    
    ObjectMapper om = new ObjectMapper();
    
    String jsonResponse;
    
    String messageMLResponse;

    String streamId; 
    
    @Before
    public void setup() throws IOException {
        this.parser = new KoreAIResponseHandlerImpl(api, rl, om, true);
        Mockito.when(api.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull()))
        	.then((a) -> {
        		streamId = a.getArgument(1);
        		messageMLResponse = a.getArgument(2);
        		jsonResponse = a.getArgument(3);
        		return null;
        	});
        jsonResponse = null;
        messageMLResponse = null;
        streamId = null;
    }

    private String contents(String filename) throws IOException {
    	return StreamUtils.copyToString(
        		asInputStream(filename), Charset.defaultCharset());
    }

	private InputStream asInputStream(String filename) throws IOException {
		return rl.getResource("classpath:/"+filename).getInputStream();
	}

    @Test
    public void testFormAnswer() throws JsonMappingException, JsonProcessingException, IOException {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "abc1234");
    	KoreAIResponse resp = om.readValue(contents("response-in.json"), KoreAIResponse.class);
        this.parser.handle(a, resp);
        Assert.assertEquals(contents("templates/koreai-form.ftl"), messageMLResponse);
        Assert.assertEquals(contents("response-out.json"), jsonResponse);
        Assert.assertEquals("abc1234", streamId);
    }
    
    @Test
    public void testMessageAnswer() throws JsonMappingException, JsonProcessingException, IOException {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "m3");
    	KoreAIResponse resp = om.readValue(contents("response-message.json"), KoreAIResponse.class);
        this.parser.handle(a, resp);
        Assert.assertEquals(contents("templates/koreai-message.ftl"), messageMLResponse);
        Assert.assertEquals(contents("response-message-out.json"), jsonResponse);
        Assert.assertEquals("m3", streamId);
    }
    
    @Test
    public void testNoAnswer() throws JsonMappingException, JsonProcessingException, IOException {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "abc1234");
    	KoreAIResponse resp = om.readValue(contents("no-answer.json"), KoreAIResponse.class);
        this.parser.handle(a, resp);
        Assert.assertNull(messageMLResponse);
        Assert.assertNull(jsonResponse);
        Assert.assertNull(streamId);
    }
}
