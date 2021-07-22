package org.finos.symphony.toolkit.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.User;
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = { 
		AbstractMockSymphonyTest.MockConfiguration.class, 
	SymphonyWorkflowConfig.class,
})
public abstract class AbstractMockSymphonyTest {

	private static final String BOT_EMAIL = "dummybot@example.com";
	
	@MockBean
	MessagesApi messagesApi;
	
	@MockBean
	DatafeedApi datafeedApi;
	
	@MockBean
	RoomMembershipApi rmApi;
	
	@MockBean
	StreamsApi streamsApi;
	
	@Autowired
	ResponseHandlers rh;
	
	@BeforeEach
	public void setupUsersMock() {

	}

	@Configuration
	public static class MockConfiguration {
	
		@Bean(name=SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
		public SymphonyIdentity symphonyIdentity() {
			SymphonyIdentity botIdentity = Mockito.mock(SymphonyIdentity.class);
			Mockito.when(botIdentity.getEmail()).then((i) -> BOT_EMAIL);
			return botIdentity;
		}
		
		@Bean
		public LocalValidatorFactoryBean localValidatorFactoryBean() {
			return new LocalValidatorFactoryBean();
		}
		
		@Bean
		public AttachmentHandler mockAttachmentHandler() {
			return new AttachmentHandler() {
				
				@Override
				public Object formatAttachment(AttachmentResponse ar) {
					return ar.getAttachment();
				}
			};
		}

		@Bean
		public OurController ourController() {
			return new OurController();
		}
		
		@Bean
		public UsersApi usersApi() {
			UsersApi usersApi = Mockito.mock(UsersApi.class);
			when(usersApi.v1UserGet(Mockito.eq("rob@example.com"), Mockito.isNull(), Mockito.anyBoolean()))
				.thenReturn(new User().emailAddress("rob@example.com").id(765l));
			when(usersApi.v2UserGet(Mockito.isNull(), Mockito.nullable(long.class), Mockito.eq(BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
				.thenReturn(new UserV2().emailAddress(BOT_EMAIL).id(111l));
			when(usersApi.v2UserGet(any(), any(), any(), any(), any()))
				.then(a -> new UserV2().id(45l).displayName("Roberto Banquet").emailAddress("r@example.com"));
			return usersApi;
		}
		
	}
	
	
	protected void testTemplating(WorkResponse wr, String streamId, String testStemML, String testStemJson) throws IOException, JsonMappingException, JsonProcessingException {
		rh.accept(wr);
	    testTemplating(streamId, testStemML, testStemJson);
	}


	protected void testTemplating(String streamId, String testStemML, String testStemJson)
			throws FileNotFoundException, IOException, JsonMappingException, JsonProcessingException {
		ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
	    ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);
			
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
	        
        FileOutputStream out2 = new FileOutputStream("target/tests/"+testStemJson);
        StreamUtils.copy(data.getValue(), StandardCharsets.UTF_8, out2);
	        
        Assertions.assertTrue(loadML(testStemML).contentEquals(msg.getValue()));
        compareJson(loadJson(testStemJson), data.getValue());
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
