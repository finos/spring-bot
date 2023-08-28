package org.finos.springbot.teams.handlers;

import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.conversations.TeamsErrorResourceResponse;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.microsoft.bot.connector.rest.ErrorResponseException;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ResourceResponse;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;

import retrofit2.Response;

@SpringBootTest(classes = { MockTeamsConfiguration.class, TeamsWorkflowConfig.class, DataHandlerConfig.class })
@ActiveProfiles("teams")
@ExtendWith(SpringExtension.class)
public class TeamsResponseHandlerTest {

	@MockBean
	ActivityHandler ah;

	@MockBean
	ResponseHandlers eh;

	@Autowired
	TeamsResponseHandler teamsResponseHandler;

	@Test
	public void testErrorIsReturned() {
		
		ArgumentCaptor<Activity> msg = ArgumentCaptor.forClass(Activity.class);

		Mockito.when(ah.handleActivity(msg.capture(), Mockito.any())).thenAnswer(a -> {
			// fails
			ResponseBody out = ResponseBody.create("{}", MediaType.get("application/json"));
			Response<ResponseBody> r = Response.error(out,
					new okhttp3.Response.Builder().code(HttpStatus.NOT_FOUND.value())
							.message("{\"error\":{\"code\": \"ConversationNotFound\",\"message\":\"Conversation not found.\"}}")
							.protocol(Protocol.HTTP_1_1).request(new Request.Builder().url("http://localhost/").build())
							.build());
			return failed(new ErrorResponseException("Failed", r));
		});

		TeamsUser tu = new TeamsUser("made", "up", "thing");
		MessageResponse r = new MessageResponse(tu, "Some object");
		ResourceResponse rr = teamsResponseHandler.apply(r);

		Assertions.assertTrue(rr instanceof TeamsErrorResourceResponse);
		Assertions.assertNotNull(((TeamsErrorResourceResponse) rr).getThrowable());
	}

	public static <R> CompletableFuture<R> failed(Throwable error) {
		CompletableFuture<R> future = new CompletableFuture<>();
		future.completeExceptionally(error);
		return future;
	}

}
