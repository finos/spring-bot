package org.finos.symphony.toolkit.workflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.model.MemberInfo;
import com.symphony.api.model.MembershipList;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = { 
		SymphonyMockConfiguration.class, 
	SymphonyWorkflowConfig.class,
})
public abstract class AbstractMockSymphonyTest {

	public static final String BOT_EMAIL = "dummybot@example.com";
	public static final long BOT_ID = 654321l;
	public static final String ROB_EXAMPLE_EMAIL = "rob@example.com";
	public static final long ROB_EXAMPLE_ID = 765l;
	public static final String ROB_NAME =  "Robert Moffat";


	protected void testTemplating(WorkResponse wr, String streamId, String testStemML, String testStemJson) throws IOException, JsonMappingException, JsonProcessingException {
		rh.accept(wr);
	    testTemplating(streamId, testStemML, testStemJson);
	}


	protected String testTemplating(String streamId, String testStemML, String testStemJson)
			throws FileNotFoundException, IOException, JsonMappingException, JsonProcessingException {
		ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
	    ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);
	    
        String expectedJson = loadJson(testStemJson);
        String expectedML = loadML(testStemML);
			
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), 
				Mockito.matches(streamId),
				msg.capture(),
				data.capture(),
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.isNull());
		        
	       System.out.println(data.getValue());
	        
	    new File("target/tests").mkdirs();
	        
        FileOutputStream out1 = new FileOutputStream("target/tests/"+testStemML);
        StreamUtils.copy(msg.getValue(), StandardCharsets.UTF_8, out1);
        
        FileOutputStream out1ex = new FileOutputStream("target/tests/"+testStemML+".expected");
        StreamUtils.copy(expectedML, StandardCharsets.UTF_8, out1ex);
	        
        FileOutputStream out2 = new FileOutputStream("target/tests/"+testStemJson);
        StreamUtils.copy(data.getValue(), StandardCharsets.UTF_8, out2);

        FileOutputStream out2ex = new FileOutputStream("target/tests/"+testStemJson+".expected");
        StreamUtils.copy(expectedJson, StandardCharsets.UTF_8, out2ex);

        Assertions.assertTrue(expectedML.contentEquals(msg.getValue()));
		compareJson(expectedJson, data.getValue());
        
        return data.getValue();
	}	

    private String loadML(String string) throws IOException {
        return StreamUtils.copyToString(AbstractMockSymphonyTest.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
    }

    private String loadJson(String string) throws IOException {
        return StreamUtils.copyToString(AbstractMockSymphonyTest.class.getResourceAsStream(string), Charset.forName("UTF-8"));
    }

	
	
    protected void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
    }


	protected WorkResponse createWorkAddSubmit(WorkMode wm, Object ob5) {
		SymphonyRoom theRoom = new SymphonyRoom("tesxt room", "abc123");
		WorkResponse wr = new WorkResponse(theRoom, ob5, wm);
		ButtonList bl = (ButtonList) wr.getData().get(ButtonList.KEY);
		Button submit = new Button("submit", Type.ACTION, "GO");
		bl.add(submit);
		return wr;
	}
}
