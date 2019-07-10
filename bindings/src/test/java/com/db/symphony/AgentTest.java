package com.db.symphony;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.experimental.theories.Theory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.Streams;
import com.symphony.api.Streams.Worker;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.agent.SystemApi;
import com.symphony.api.model.Datafeed;
import com.symphony.api.model.V2HealthCheckResponse;
import com.symphony.api.model.V4Event;

/**
 * Tests of some Agent endpoints.
 * 
 * @author moffrob
 *
 */
public class AgentTest extends AbstractTest {

	public static String asString(InputStream is) {
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}	
	}
	
	@Theory
	public void testDataPost(TestClientStrategy s) throws Exception {
		
		MessagesApi messageAPi = s.getAgentApi(MessagesApi.class);
		String in = asString(this.getClass().getResourceAsStream("/pizza.json"));
		
		messageAPi.v4StreamSidMessageCreatePost(null, ROOM,
				"<messageML>" + 
				"  Hello. Here is an important message with an" + 
				"  <div class=\"entity\" data-entity-id=\"object001\" />" + 
				"  included." + 
				"</messageML>", in, null, null, null, null);
	}
	
	@Theory
	public void testStreams(TestClientStrategy s) throws Exception {
		DatafeedApi dfApi = s.getAgentApi(DatafeedApi.class);
		MessagesApi messageAPi = s.getAgentApi(MessagesApi.class);

		Datafeed datafeed = dfApi.v4DatafeedCreatePost(null, null);

		System.out.println("Datafeed ID: "+datafeed.getId());

		Supplier<List<V4Event>> supplier = () -> dfApi.v4DatafeedIdReadGet(datafeed.getId(), null, null, 100);

		final int[] count = { 0 };
		final Worker<V4Event> w = Streams.createWorker(supplier, e -> e.printStackTrace());
		
		Thread t = new Thread(() -> {
			w.stream().forEach(e -> count[0]++);
		});

		t.setDaemon(true);
		t.start();

		messageAPi.v4StreamSidMessageCreatePost(null, ROOM,
				"<messageML>Trigger Listener.</messageML>", null, null, null, null, null);

		// wait for roundtrip
		while (count[0] == 0) {
			Thread.yield();
		}
	}

	@Theory
	public void testHealthEndpoint(TestClientStrategy s) throws Exception {
		SystemApi systemApi = s.getAgentApi(SystemApi.class);
		V2HealthCheckResponse resp = systemApi.v2HealthCheckGet(null, null);
		String json = new ObjectMapper().writeValueAsString(resp);
		Assert.assertTrue(resp.isisPodConnectivity());
		Assert.assertTrue(resp.isisKeyManagerConnectivity());
		Assert.assertTrue(resp.isisAgentServiceUser());
		System.out.println(json);
	}
	
}
