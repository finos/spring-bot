package com.symphony.api.bindings;

import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.agent.SystemApi;
import com.symphony.api.bindings.Streams.Worker;
import com.symphony.api.model.AckId;
import com.symphony.api.model.Datafeed;
import com.symphony.api.model.V2Error;
import com.symphony.api.model.V3Health;
import com.symphony.api.model.V3HealthStatus;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V5Datafeed;
import com.symphony.api.model.V5DatafeedCreateBody;
import com.symphony.api.model.V5EventList;

/**
 * Tests of some Agent endpoints.
 * 
 * @author moffrob
 *
 */
public class AgentIT extends AbstractIT {

	public static String asString(InputStream is) {
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}	
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testDataPost(TestClientStrategy s) throws Exception {
		
		MessagesApi messageAPi = s.getAgentApi(MessagesApi.class);
		String in = asString(this.getClass().getResourceAsStream("/pizza.json"));
		
		V4Message done = messageAPi.v4StreamSidMessageCreatePost(null, ROOM,
				"<messageML>" + 
				"  Hello. Here is an important message with an" + 
				"  <div class=\"entity\" data-entity-id=\"object001\" />" + 
				"  included." + 
				"</messageML>", in, null, null, null, null);
		
		// updating messages currently not supported on develop pod
//		// try updating the message
//		V4Message second = messageAPi.v4StreamSidMessageMidUpdatePost(null, ROOM, done.getMessageId(), "<messageML>This is updated</messageML>", in, null, null);
//		
//		// read the message back
//		V4Message third = messageAPi.v1MessageIdGet(null, null, done.getMessageId());
//		
//		Assertions.assertEquals(second.getMessage(), third.getMessage());
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testStreamsV4(TestClientStrategy s) throws Exception {
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

		String toSend = "Trigger Listener."+new Random().nextInt();
		messageAPi.v4StreamSidMessageCreatePost(null, ROOM, "<messageML>"+toSend+"</messageML>", null, null, null, null, null);

		// wait for roundtrip
		while (count[0] == 0) {
			Thread.yield();
		}
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testBlastAPI(TestClientStrategy s) throws Exception {
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

		String toSend = "Trigger Listener."+new Random().nextInt();
		messageAPi.v4MessageBlastPost(ROOM+","+ROOM, "<messageML>"+toSend+"</messageML>", null, null, null, null, null, null);

		// wait for roundtrip
		while (count[0] == 0) {
			Thread.yield();
		}
		
	}
	
	class V5Supplier implements Supplier<List<V4Event>> {
		
		String lastAck = "";
		
		V5Datafeed datafeed;
		private DatafeedApi api;

		public V5Supplier(DatafeedApi api, V5Datafeed datafeed) {
			super();
			this.api = api;
			this.datafeed = datafeed;
		}

		@Override
		public List<V4Event> get() {
			if (datafeed != null) {
				AckId ackId = new AckId().ackId(lastAck);
				System.out.println("waiting for messages");
				V5EventList el = api.readDatafeed(null, null, datafeed.getId(), ackId);
				System.out.println("Recieved event list containing "+el.getEvents().size()+" events ackId "+el.getAckId());
				this.lastAck = el.getAckId()+"grf";
				return el.getEvents();
			} else {
				return null;
			}
		}
		
		
	}

	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testStreamsV5(TestClientStrategy s) throws Exception {
		DatafeedApi dfApi = s.getAgentApi(DatafeedApi.class);
		MessagesApi messageAPi = s.getAgentApi(MessagesApi.class);

		// ensure a clean slate
		dfApi.listDatafeed(null, null, "testy").stream()
			.forEach(df -> dfApi.deleteDatafeed(df.getId(), null, null));

		// create the new datafeed
		V5DatafeedCreateBody body = new V5DatafeedCreateBody();
		body.setTag("testy");
		V5Datafeed datafeed = dfApi.createDatafeed(null, null, body);
		System.out.println("Datafeed ID: "+datafeed.getId());
		
		// should be able to list
		List<V5Datafeed> foundFeeds = dfApi.listDatafeed(null, null, "testy");
		Assertions.assertEquals(1, foundFeeds.size());
		Assertions.assertEquals(datafeed.getId(), foundFeeds.get(0).getId());

		V5Supplier supplier = new V5Supplier(dfApi, datafeed);

		final int[] count = { 0 };
		final Worker<V4Event> w = Streams.createWorker(supplier, e -> e.printStackTrace());
		
		Thread t = new Thread(() -> {
			w.stream().forEach(e -> count[0]++);
		});

		t.setDaemon(true);
		t.start();

		Thread.sleep(10000);
		
		String toSend = "Trigger Listener."+new Random().nextInt();
		messageAPi.v4StreamSidMessageCreatePost(null, ROOM, "<messageML>"+toSend+"</messageML>", null, null, null, null, null);
		System.out.println("Wrote message");
		
		// wait for roundtrip
		while (count[0] == 0) {
			messageAPi.v4StreamSidMessageCreatePost(null, ROOM, "<messageML>"+toSend+"</messageML>", null, null, null, null, null);
			Thread.yield();
		}
		
		supplier.datafeed = null;
		
		// try deleting the datafeed
		V2Error error = dfApi.deleteDatafeed(datafeed.getId(), null, null);
		Assertions.assertNull(error);
		
		// should be able to list
		foundFeeds = dfApi.listDatafeed(null, null, "testy");
		Assertions.assertEquals(0, foundFeeds.size());
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testHealthEndpoint(TestClientStrategy s) throws Exception {
		SystemApi systemApi = s.getAgentApi(SystemApi.class);
		V3Health v3Health = systemApi.v3ExtendedHealth();
		String json = new ObjectMapper().writeValueAsString(v3Health);
		Assertions.assertTrue(v3Health.getStatus().equals(V3HealthStatus.UP));
		Assertions.assertTrue(v3Health.getServices().get("pod").getStatus().equals(V3HealthStatus.UP));
		Assertions.assertTrue(v3Health.getServices().get("key_manager").getStatus().equals(V3HealthStatus.UP));
		Assertions.assertTrue(v3Health.getUsers().get("agentservice").getStatus().equals(V3HealthStatus.UP));
		System.out.println(json);
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testFailingCall(TestClientStrategy s) throws Exception {
		try {
			MessagesApi messageAPI = s.getAgentApi(MessagesApi.class);
			messageAPI.v4StreamSidMessageGet("sfjkd", 100l, null, null, 100, 100);
			Assertions.fail("Shouldn't get here - the stream is invalid");
		} catch (Exception e) {
			Assertions.assertTrue(e.getMessage().contains("Bad Request"));
			Assertions.assertTrue(e.getMessage().contains("\"message\":\"This thread doesn't exist.\""));
		}
	}
	
}
