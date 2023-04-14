package org.finos.springbot.teams.handlers.retry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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

@ActiveProfiles("teams")
@ExtendWith(SpringExtension.class)
public class InMemoryRetryingActivityHandlerTest {

	@MockBean
	TeamsConversations conv;

	int go = 0;

	@BeforeEach
	public void mockSetup() {
		ArgumentCaptor<Activity> msg = ArgumentCaptor.forClass(Activity.class);

		Mockito.when(conv.handleActivity(msg.capture(), Mockito.any())).thenAnswer(a -> {
			ResourceResponse arg1 = new ResourceResponse("done");

			if (go % 3 != 2) {
				// fails
				ResponseBody out = ResponseBody.create("{}", MediaType.get("application/json"));
				Response<ResponseBody> r = Response.error(out,
						new okhttp3.Response.Builder()
								.code(HttpStatus.TOO_MANY_REQUESTS.value())
								.message("Response.error()").addHeader("Retry-After", "1000")
								.protocol(Protocol.HTTP_1_1)
								.request(new Request.Builder().url("http://localhost/").build()).build());
				go++;
				return CompletableFuture.failedFuture(new ErrorResponseException("Failed", r));
			} else {
				go++;
				return CompletableFuture.completedFuture(arg1);
			}
		});
	}

	TeamsChannel dummyChat1 = new TeamsChannel("dummy_id_1", "dummy_name");

	@Test
	public void testRetryWorks() throws InterruptedException, ExecutionException {
		long now = System.currentTimeMillis();

		RetryingActivityHandler retry = new RetryingActivityHandler(conv);

		CompletableFuture<ResourceResponse> cf = retry.handleActivity(new Activity("dummy"), dummyChat1);

		ResourceResponse rr = cf.get();

		Assertions.assertEquals("done", rr.getId());
		long doneTime = System.currentTimeMillis();

		Assertions.assertTrue(doneTime - now > 2000);
		Assertions.assertEquals(3, go);

	}

}
