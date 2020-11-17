package org.finos.symphony.toolkit.koreai.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilder;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilderImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

/**
 * @author rodriva
 */
@RunWith(SpringRunner.class)
public class KoreAIResponseHandlerImplTest {

    private KoreAIResponseHandler output;
    private KoreAIResponseBuilder builder;
    
    @MockBean
    MessagesApi api;
    
    @MockBean
    SymphonyIdentity id;
    
    @Autowired
    ResourceLoader rl;
       
    @MockBean
    UsersApi usersApi;
    
    List<String> jsonResponse;
    
    List<String> messageMLResponse;

    List<String> streamId; 
    
    ObjectMapper om;
    
    @Before
    public void setup() throws Exception {
       	om = new ObjectMapper();
    		ObjectMapperFactory.initialize(om, ObjectMapperFactory
    			.extendedSymphonyVersionSpace(
    				new VersionSpace(KoreAIResponse.class.getPackage().getName(), "1.0")));

    	this.builder = new KoreAIResponseBuilderImpl(new ObjectMapper(), JsonNodeFactory.instance);
        this.output = new KoreAIResponseHandlerImpl(api, rl, true, om, "classpath:/test-templates");
        Mockito.when(api.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull()))
        	.then((a) -> {
        		streamId.add(a.getArgument(1));
        		messageMLResponse.add(a.getArgument(2));
        		jsonResponse.add(a.getArgument(3));
        		return null;
        	});
        jsonResponse = new ArrayList<String>();
        messageMLResponse = new ArrayList<String>();
        streamId = new ArrayList<String>();
        
     }

    private String contents(String filename) throws IOException {
    	return StreamUtils.copyToString(
        		asInputStream(filename), Charset.defaultCharset());
    }

	private InputStream asInputStream(String filename) throws IOException {
		return KoreAIResponseHandlerImplTest.class.getResourceAsStream(filename);
	}

    @Test
    public void testFormAnswer() throws Exception {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "abc1234");
    	KoreAIResponse resp = builder.formatResponse(contents("response-form.json"));
        this.output.handle(a, resp);
        Assert.assertEquals(contents("/templates/default/koreai-message.ftl"), messageMLResponse);
        System.out.println(jsonResponse);
        Assert.assertEquals(contents("response-form-out.json"), jsonResponse);
        Assert.assertEquals("abc1234", streamId);
    }
    
    @Test
    public void testMessageAnswer() throws Exception {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "m3");
    	KoreAIResponse resp =builder.formatResponse(contents("response-message.json"));
        this.output.handle(a, resp);
        Assert.assertEquals(contents("/templates/default/koreai-message.ftl"), messageMLResponse.get(0));
        System.out.println(jsonResponse);
        Assert.assertEquals(contents("response-message-out.json"), jsonResponse.get(0));
        Assert.assertEquals("m3", streamId.get(0));
    }
    
    @Test
    public void testTemplating() throws Exception {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "m3");
    	KoreAIResponse resp =builder.formatResponse(contents("templates.json"));
        this.output.handle(a, resp);
        
        for (int i = 0; i < jsonResponse.size(); i++) {
			String json = jsonResponse.get(i);
			String messsageML = messageMLResponse.get(i);
			Configuration c = new Configuration(new Version("2.3.30"));
			Template t = new Template("bs", new StringReader(messsageML), c);
			StringWriter out = new StringWriter();
			JsonNode o = om.readTree(json);
			t.process(o, out);
        	System.out.println(out);
        	
        	 Assert.assertEquals(contents("/templates/default/koreai-message.ftl"), messageMLResponse.get(0));
             System.out.println(jsonResponse);
             Assert.assertEquals(contents("response-message-out.json"), jsonResponse.get(0));
             Assert.assertEquals("m3", streamId.get(0));
		}
        
       
    }
    
    @Test
    public void testNoAnswer() throws Exception {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "abc1234");
    	KoreAIResponse resp = builder.formatResponse(contents("no-answer.json"));
        this.output.handle(a, resp);
        Assert.assertEquals(0, messageMLResponse.size());
        Assert.assertEquals(0, jsonResponse.size());
        Assert.assertEquals(0, streamId.size());
    } 
}
