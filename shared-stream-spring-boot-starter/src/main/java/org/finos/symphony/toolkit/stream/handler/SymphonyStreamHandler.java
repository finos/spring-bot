package org.finos.symphony.toolkit.stream.handler;

import java.util.List;
import java.util.function.Consumer;

import javax.ws.rs.BadRequestException;

import org.finos.symphony.toolkit.spring.api.ApiInstance;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.bindings.Streams;
import com.symphony.api.bindings.Streams.Worker;
import com.symphony.api.model.V4Event;

/**
 * Robust symphony stream handler, which restarts in the event of symphony downtime/crashes.
 * 
 * @author robmoffat
 *
 */
public class SymphonyStreamHandler {
	
	public static final long MIN_BACK_OFF_MS = 2000;   // 2 secs
	public static final long MAX_BACK_OFF_MS = 600000;	// 10 mins
	
	protected final ApiInstance instance;
	protected final Consumer<V4Event> consumer;
	protected final DatafeedApi datafeedApi;
	protected final Consumer<Exception> exceptionHandler;

	protected Thread runThread;
	protected long currentBackOff = MIN_BACK_OFF_MS;
	protected Worker<V4Event> worker;
	protected StreamEventFilter filter = (x) -> true;
	protected boolean running = false;

	public SymphonyStreamHandler(ApiInstance api,
			Consumer<V4Event> eventConsumer, 
			Consumer<Exception> exceptionHandler, boolean start) {
		this.datafeedApi = api.getAgentApi(DatafeedApi.class);
		this.consumer = eventConsumer;
		this.exceptionHandler = exceptionHandler;
		if (start) {
			start();
		}
		this.instance = api;
	}
	
	public SymphonyStreamHandler(ApiInstance api,
			List<? extends Consumer<V4Event>> eventConsumers, 
			Consumer<Exception> exceptionHandler, boolean start) {
		this(api, multiConsumer(eventConsumers), exceptionHandler, start);
	}

	/**
	 * The multi-consumer passes each event to a list of consumers.  If any fails, the rest of the consumers in the
	 * list will not get the message.
	 */
	private static Consumer<V4Event> multiConsumer(List<? extends Consumer<V4Event>> eventConsumers) {
		return new Consumer<V4Event>() {

			@Override
			public void accept(V4Event t) {
				for (Consumer<V4Event> consumer : eventConsumers) {
					consumer.accept(t);
				}
			}
		};
	}

	/**
	 * Uses a daemon thread to start the process.  Use something else if you want
	 */
	public void start() {
		if (running == false) {
			running = true;
			String initialDatafeedId = datafeedApi.v4DatafeedCreatePost(null, null).getId();
			runThread = new Thread(() -> consumeLoop(initialDatafeedId));
			runThread.setDaemon(true);
			runThread.setName("SymphonyStream");
			runThread.start();
		}
	}

	public void consumeLoop(String initialDatafeedId) {
		while (running) {
			String[] theId = { initialDatafeedId };
			try {
				worker = Streams.createWorker(
						() -> datafeedApi.v4DatafeedIdReadGet(theId[0], null, null, 50),
						e -> {
							exceptionHandler.accept(e);
							if (e instanceof BadRequestException) {
								theId[0] = datafeedApi.v4DatafeedCreatePost(null, null).getId();
								backOff();
							}
						});
				
				worker.stream().forEach(event -> { 
					if (filter.test(event)) {
						sendToConsumer(event);
					}
					currentBackOff = MIN_BACK_OFF_MS;		
				});
			} catch (Exception e) {
				exceptionHandler.accept(e);
			}
		}
	}

	/**
	 * After a problem with the datafeed, we back-off, in order that we don't spam the exception handler with 
	 * too many exception messages
	 */
	protected void backOff() {
		try {
			Thread.sleep(currentBackOff);
			currentBackOff = Math.min(MAX_BACK_OFF_MS, currentBackOff * 2);
		} catch (InterruptedException e) {
			exceptionHandler.accept(e);
		}
	}

	public void sendToConsumer(V4Event event) {
		consumer.accept(event);
	}
	
	public void stop() {
		running = false;
		worker.shutdown();
		runThread.interrupt();
	}

	public StreamEventFilter getFilter() {
		return filter;
	}

	public void setFilter(StreamEventFilter filter) {
		this.filter = filter;
	}
	
	public ApiInstance getInstance() {
		return instance;
	}


}
