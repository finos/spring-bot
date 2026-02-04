package org.finos.springbot.teams.controller;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.bot.BotController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.microsoft.bot.builder.Bot;
import com.microsoft.bot.builder.InvokeResponse;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.schema.Activity;

@SpringBootTest(classes = { MockTeamsConfiguration.class})
@ActiveProfiles("teams")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class BotControllerTest {

	@Mock
	BotFrameworkHttpAdapter adapter;
	
	@Mock
	Bot bot;
	
	BotController controller;
	
	@BeforeEach
	public void setup() {
		// Explicitly construct controller with mocks to ensure non-null final fields
		controller = new BotController(adapter, bot);
	}

	@SuppressWarnings({ "deprecation" })
	@Test
	public void testIncoming() throws InterruptedException, ExecutionException {
		Activity a = Mockito.mock(Activity.class);
		InvokeResponse ir = new InvokeResponse(HttpStatus.OK.value(), "Success");
		Mockito.when(adapter.processIncomingActivity("any text", a, bot)).thenReturn(CompletableFuture.completedFuture(ir));
		CompletableFuture<ResponseEntity<Object>> future = controller.incoming(a, "any text");
		
		ResponseEntity<Object> entity = future.get();

		Assertions.assertEquals(200, entity.getStatusCode().value());
		Assertions.assertEquals("Success", entity.getBody());
		
	}
	
}
