package org.finos.symphony.toolkit.stream.handler;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.id.PemSymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.testing.TestIdentityProvider;
import com.symphony.api.model.AckId;
import com.symphony.api.model.Datafeed;
import com.symphony.api.model.MessageList;
import com.symphony.api.model.V2Error;
import com.symphony.api.model.V2MessageList;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4EventList;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V5Datafeed;
import com.symphony.api.model.V5EventList;

public class SymphonyStreamHandlerIT {
	
	List<V4Event> events = new ArrayList<V4Event>();
	List<Exception> exceptions = new ArrayList<Exception>();
	
	@BeforeEach
	public void clear() {
		events.clear();
		exceptions.clear();
	}
	
	class RuntimeInterruptedException extends RuntimeException {
	}

	
	class DummyDatafeedApi implements DatafeedApi {
		
		int currentId = 868;
		int call = 0;
		int eventId = 0;

		@Override
		public Datafeed v1DatafeedCreatePost(String sessionToken, String keyManagerToken) {
			return new Datafeed().id(""+(++currentId));
		}

		@Override
		public MessageList v1DatafeedIdReadGet(String id, String sessionToken, String keyManagerToken,
				Integer maxMessages) {
			return null; // not used
		}

		@Override
		public V2MessageList v2DatafeedIdReadGet(String id, String sessionToken, String keyManagerToken,
				Integer maxMessages) {
			return null; // not used
		}

		@Override
		public Datafeed v4DatafeedCreatePost(String sessionToken, String keyManagerToken) {
			return new Datafeed().id(""+(++currentId));
		}

		@Override
		public V4EventList v4DatafeedIdReadGet(String id, String sessionToken, String keyManagerToken, Integer limit) {
			if (id.equals(""+currentId)) {
				return doInnerEventList();
			} else {
				throw new RuntimeException("Wrong id");
			}
		}

		protected V4EventList doInnerEventList() {
			if (call == 0) {
				V4Event e = createEvent();
				V4EventList out = new V4EventList();
				out.add(e);
				call++;
				return out;
			} else {
				sleep(1000);
				return null;
			}
		}

		protected V4Event createEvent() {
			return new V4Event().id(""+(eventId++)).payload(
					new V4Payload().messageSent(
							new V4MessageSent().message(
								new V4Message().message("hi"))));
		}

		@Override
		public V5Datafeed createDatafeed(String sessionToken, String keyManagerToken) {
			return null; // not used
		}

		@Override
		public V2Error deleteDatafeed(String datafeedId, String sessionToken, String keyManagerToken) {
			return null; // not used
		}

		@Override
		public List<V5Datafeed> listDatafeed(String sessionToken, String keyManagerToken) {
			return null; // not used
		}

		@Override
		public V5EventList readDatafeed(String sessionToken, String keyManagerToken, String datafeedId, AckId body) {
			return null; // not used
		}
	}
	
	protected ApiInstance dummyApiInstance(DatafeedApi api) {
		return new ApiInstance() {
			
			@Override
			public <X> X getSessionAuthApi(Class<X> c) { return null; }
			
			@Override
			public <X> X getRelayApi(Class<X> c) { return null; }
			
			@Override
			public <X> X getPodApi(Class<X> c) { return null; }
			
			@Override
			public <X> X getLoginApi(Class<X> c) { return null; }
			
			@Override
			public <X> X getKeyAuthApi(Class<X> c) { return null; }
			
			@Override
			public SymphonyIdentity getIdentity() { return TestIdentityProvider.getTestIdentity(); }
			
			@Override
			public <X> X getAgentApi(Class<X> c) { 
				if (c==DatafeedApi.class) {
					return (X) api;
				} else {
					return null;
				}
			}
		};
	}
	
	
	protected void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException();
		}
	}


	@Test
	public void testGeneralOperation() throws InterruptedException {
		SymphonyStreamHandler ssh = new SymphonyStreamHandler(dummyApiInstance(
				new DummyDatafeedApi()), 
				v -> events.add(v), 
				e -> exceptions.add(e), true);
		Thread.sleep(50);
		Assertions.assertEquals(1, events.size());
		Assertions.assertEquals(0, exceptions.size());
		ssh.stop();
	}
	
	@Test
	public void testStopOperation() throws InterruptedException {
		DummyDatafeedApi df = new DummyDatafeedApi() {
			protected V4EventList doInnerEventList() {
				sleep(150000);
				throw new RuntimeException("Shouldn't get here");
			}
		};
		
		SymphonyStreamHandler ssh = new SymphonyStreamHandler(dummyApiInstance(df), v -> events.add(v), e -> exceptions.add(e), false);
		ssh.start();
		Thread t = ssh.runThread;
		Assertions.assertEquals(true, t.isAlive());
		Thread.sleep(50);
		ssh.stop();
		Thread.sleep(50);
		Assertions.assertEquals(0, events.size());
		Assertions.assertEquals(1, exceptions.size());
		Assertions.assertEquals(false, t.isAlive());
		Assertions.assertTrue(exceptions.get(0) instanceof RuntimeInterruptedException);
	}
	
	@Test
	public void testDodgyMessageProcessing() throws InterruptedException {
		DummyDatafeedApi df = new DummyDatafeedApi() {
			
			/**
			 * Just continuously spew messages
			 */
			public V4EventList v4DatafeedIdReadGet(String id, String sessionToken, String keyManagerToken, Integer limit) {
				V4EventList out = new V4EventList();
				out.add(createEvent());
				out.add(createEvent());
				out.add(createEvent());
				out.add(createEvent());
				out.add(createEvent());
				return out;
			}
		};
		
		SymphonyStreamHandler ssh = new SymphonyStreamHandler(dummyApiInstance(df), v -> {
			if (v.getId().contains("5")) {
				throw new IllegalArgumentException("Couldn't process: "+v.getId());
			} else {
				events.add(v);
			}
		}, e -> exceptions.add(e), true);
		Thread.sleep(200);
		
		ssh.stop();
		
		// we should still be on the first datafeed
		Assertions.assertEquals(869, df.currentId);
		
		// assert no back-off for user errors
		Assertions.assertTrue(events.size() > 20);
		
		// assert that there are errors in the log
		Assertions.assertFalse(exceptions.isEmpty());
	}
	
	@Test
	public void testStreamDying() throws InterruptedException {
		DummyDatafeedApi df = new DummyDatafeedApi() {
			
			int readFrom = 0;
			/**
			 * You can read from each datafeed only once, before it dies
			 */
			public V4EventList v4DatafeedIdReadGet(String id, String sessionToken, String keyManagerToken, Integer limit) {
				if (readFrom == currentId) {
					throw new BadRequestException("broken");
				} else {
					readFrom = currentId;
					V4EventList out = new V4EventList();
					out.add(createEvent());
					out.add(createEvent());
					out.add(createEvent());
					out.add(createEvent());
					out.add(createEvent());
					return out;
				}
			}
		};
		
		SymphonyStreamHandler ssh = new SymphonyStreamHandler(dummyApiInstance(df), v -> events.add(v), e -> exceptions.add(e), true);
		Thread.sleep(4000);
		ssh.stop();
		
		// we should be after the first datafeed
		Assertions.assertTrue(df.currentId > 869);
		
		// check we've read lots of events
		Assertions.assertTrue(events.size() > 5);
		
		// should be errors from the datafeed dying
		Assertions.assertFalse(exceptions.isEmpty());
		Assertions.assertTrue(exceptions.get(0) instanceof BadRequestException);
	}
	
	@Test
	public void testSymphonyDeadOnStartup() {
		Assertions.assertThrows(BadRequestException.class, () -> {
			DummyDatafeedApi df = new DummyDatafeedApi() {
	
				/**
				 * Stream handler should throw exception on startup
				 */
				@Override
				public Datafeed v4DatafeedCreatePost(String sessionToken, String keyManagerToken) {
					throw new BadRequestException();
				}
			};
			
			SymphonyStreamHandler ssh = new SymphonyStreamHandler(dummyApiInstance(df), v -> events.add(v), e -> exceptions.add(e), true);
			ssh.stop();
		});
	}
	
	@Test
	public void testBackoffExceptionSpam() throws InterruptedException {
		DummyDatafeedApi df = new DummyDatafeedApi() {
			
			/**
			 * Continually throw exceptions
			 */
			public V4EventList v4DatafeedIdReadGet(String id, String sessionToken, String keyManagerToken, Integer limit) {
				throw new BadRequestException("broken");
			}
		};
		
		SymphonyStreamHandler ssh = new SymphonyStreamHandler(dummyApiInstance(df), v -> events.add(v), e -> exceptions.add(e), true);
		Thread.sleep(10000);
		ssh.stop();
		// initial back-off is 2 secs, but we should only see 3 exceptions in the log.
		Assertions.assertTrue(exceptions.size() == 3);
		
	}
}
