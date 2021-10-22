package org.finos.springbot.teams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.handlers.AttachmentHandler;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.controller.OurController;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.TurnContext;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = { 
		AbstractMockTeamsTest.MockConfiguration.class, 
	TeamsWorkflowConfig.class,
})
public abstract class AbstractMockTeamsTest {

	public static final String BOT_EMAIL = "dummybot@example.com";
	public static final long BOT_ID = 654321l;
	public static final String ROB_EXAMPLE_EMAIL = "rob@example.com";
	public static final long ROB_EXAMPLE_ID = 765l;
	public static final String ROB_NAME =  "Robert Moffat";

	@Autowired
	ResponseHandlers rh;
	
//	@BeforeEach
//	public void setupMembershipMock() {
//		MembershipList out = new MembershipList();
//		out.add(new MemberInfo().id(BOT_ID).owner(false));
//		out.add(new MemberInfo().id(ROB_EXAMPLE_ID).owner(true));
//		
//		Mockito.when(rmApi.v2RoomIdMembershipListGet(Mockito.anyString(), Mockito.isNull()))
//			.thenReturn(out);
//	}

	@Configuration
	public static class MockConfiguration {
			
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
		
//		@Bean
//		public UsersApi usersApi() {
//			UsersApi usersApi = Mockito.mock(UsersApi.class);
//			
//			when(usersApi.v2UserGet(Mockito.isNull(), Mockito.isNull(), Mockito.eq(BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
//				.thenReturn(new UserV2().emailAddress(BOT_EMAIL).id(BOT_ID));
//			
//			when(usersApi.v2UserGet(Mockito.isNull(), Mockito.isNull(), Mockito.eq(ROB_EXAMPLE_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
//				.thenReturn(new UserV2().emailAddress(ROB_EXAMPLE_EMAIL).id(ROB_EXAMPLE_ID));
//			
//			when(usersApi.v2UserGet(Mockito.isNull(), Mockito.nullable(long.class), Mockito.eq(BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
//				.thenReturn(new UserV2().emailAddress(BOT_EMAIL).id(111l));
//			
//			when(usersApi.v2UserGet(any(), any(), any(), any(), any()))
//				.then(a -> new UserV2().id(ROB_EXAMPLE_ID).displayName(ROB_NAME).emailAddress(ROB_EXAMPLE_EMAIL));
//			
//			return usersApi;
//		}
//		
	}
	
	
	protected void testTemplating(WorkResponse wr, String streamId, String testStemJson) throws IOException, JsonMappingException, JsonProcessingException {
		rh.accept(wr);
	    testTemplating(streamId, testStemJson);
	}


	protected String testTemplating(String streamId, String testStemJson)
			throws FileNotFoundException, IOException, JsonMappingException, JsonProcessingException {

		ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
	    ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);
	    
        String expectedJson = loadJson(testStemJson);
	
		        
	       System.out.println(data.getValue());
	        
	    new File("target/tests").mkdirs();
	        
     
        FileOutputStream out2 = new FileOutputStream("target/tests/"+testStemJson);
        StreamUtils.copy(data.getValue(), StandardCharsets.UTF_8, out2);

        FileOutputStream out2ex = new FileOutputStream("target/tests/"+testStemJson+".expected");
        StreamUtils.copy(expectedJson, StandardCharsets.UTF_8, out2ex);
        
        return data.getValue();
	}	

    public static String loadJson(String string) throws IOException {
        return StreamUtils.copyToString(AbstractMockTeamsTest.class.getResourceAsStream(string), Charset.forName("UTF-8"));
    }

	
	
    public static void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
    }


	protected WorkResponse createWorkAddSubmit(WorkMode wm, Object ob5) {
		TurnContext tc = Mockito.mock(TurnContext.class);
		CurrentTurnContext.CURRENT_CONTEXT.set(tc);
		
		TeamsChat theRoom = new TeamsChat( "abc123", "tesxt room");
		WorkResponse wr = new WorkResponse(theRoom, ob5, wm);
		ButtonList bl = (ButtonList) wr.getData().get(ButtonList.KEY);
		Button submit = new Button("submit", Type.ACTION, "GO");
		bl.add(submit);
		return wr;
	}
}
