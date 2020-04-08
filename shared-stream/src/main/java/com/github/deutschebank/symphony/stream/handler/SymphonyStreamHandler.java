package com.github.deutschebank.symphony.stream.handler;

import java.util.function.Consumer;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.bindings.Streams;
import com.symphony.api.model.Datafeed;
import com.symphony.api.model.V4Event;

/**
 * Robust symphony stream handler, which restarts in the event of symphony downtime/crashes.
 * 
 * @author robmoffat
 *
 */
public class SymphonyStreamHandler {

	protected boolean running = false;
	protected Consumer<V4Event> consumer;
	protected DatafeedApi datafeedApi;
	protected Consumer<Exception> exceptionHandler;
	protected Thread runThread;

	public SymphonyStreamHandler(DatafeedApi api, Consumer<V4Event> eventConsumer, Consumer<Exception> exceptionHandler, boolean start) {
		this.datafeedApi = api;
		this.consumer = eventConsumer;
		this.exceptionHandler = exceptionHandler;
		if (start) {
			start();
		}
	}

	/**
	 * Uses a daemon thread to start the process.  Use something else if you want
	 */
	public void start() {
		if (running == false) {
			running = true;
			runThread = new Thread(() -> consumeLoop());
			runThread.setDaemon(true);
			runThread.setName("SymphonyStream");
			runThread.start();
		}
	}

	public void consumeLoop() {
		while (running) {
			try {
				Datafeed df = datafeedApi.v4DatafeedCreatePost(null, null);
				Streams.createWorker(
						() -> datafeedApi.v4DatafeedIdReadGet(df.getId(), null, null, 50),
						exceptionHandler)
					.stream().forEach(event -> sendToConsumer(event));
			} catch (Exception e) {
				exceptionHandler.accept(e);
				backOff();
			}
		}
	}

	protected void backOff() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			exceptionHandler.accept(e);
		}
	}

	protected void sendToConsumer(V4Event event) {
		consumer.accept(event);
	}
	
	public void stop() {
		running = false;
		runThread.interrupt();
	}
}
