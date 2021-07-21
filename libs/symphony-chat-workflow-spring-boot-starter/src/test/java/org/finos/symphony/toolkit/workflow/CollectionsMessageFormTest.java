package org.finos.symphony.toolkit.workflow;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.fixture.Address;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.fixture.Person;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ErrorHelp;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter.Mode;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CollectionsMessageFormTest extends AbstractMockSymphonyTest {

    @Autowired
    ResponseHandlers rh;

    @Autowired
    EntityJsonConverter ejc;
    
    @Autowired
	OurController oc;


    private String loadML(String string) throws IOException {
        return StreamUtils.copyToString(CollectionsMessageFormTest.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
    }

    private String loadJson(String string) throws IOException {
        return StreamUtils.copyToString(CollectionsMessageFormTest.class.getResourceAsStream(string), Charset.forName("UTF-8"));
    }

    @Test
    public void testCollectionEditMessage() throws Exception {
    	WorkResponse wr = new WorkResponse(createAddressable(), getPerson(), WorkMode.EDIT);

        rh.accept(wr);
        
        ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);
		
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), 
				Mockito.matches("abc123"),
				msg.capture(),
				data.capture(),
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.isNull());
	        
        System.out.println(data.getValue());
        
        Assertions.assertTrue(loadML("testCollectionEditMessageML.ml").contentEquals(msg.getValue()));
        compareJson(loadJson("testCollectionEditMessage.json"), data.getValue());
    }

    private Addressable createAddressable() {
		return new SymphonyRoom("bobo", "abc123");
	}

	@Test
    public void testCollectionViewMessage() throws Exception {
		WorkResponse wr = new WorkResponse(createAddressable(), getPerson(), WorkMode.VIEW);

		rh.accept(wr);
	        
	    ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
	    ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);
			
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), 
				Mockito.matches("abc123"),
				msg.capture(),
				data.capture(),
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.isNull());
		        
	        System.out.println(data.getValue());
	        
        
        Assertions.assertTrue(loadML("testCollectionViewMessageML.ml").contentEquals(msg.getValue()));
        compareJson(loadJson("testCollectionViewMessage.json"), data.getValue());
    }
    private void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
    }

    private Person getPerson(){
        Person person = new Person(Arrays.asList("abc","pqr"), Arrays.asList(new Address("Pune"), new Address("Mumbai"), new Address("Bangalore")));
        return person;
    }
    
}

