package com.symphony.api.bindings;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Workers return streams which poll a specific Symphony endpoint, allowing you to respond to events, etc.
 * 
 * @author Rob Moffat
 *
 */
public class Streams {
	
	private Streams() {
	}

	public abstract static class Worker<X> implements Spliterator<X> {

		protected boolean running = true;

		public void shutdown() {
			this.running = false;
		}

		public Stream<X> stream() {
			return StreamSupport.stream(this, false);
		}

		@Override
		public Spliterator<X> trySplit() {
			return null;
		}

		@Override
		public long getExactSizeIfKnown() {
			return -1;
		}

		@Override
		public int characteristics() {
			return Spliterator.NONNULL | Spliterator.ORDERED;
		}
	}

	public static <X> Worker<X> createWorker(Supplier<List<X>> supplier, Consumer<Exception> errorHandler) {
		final List<X> currentBuffer = new ArrayList<>();

		return new Worker<X>() {

			@Override
			public boolean tryAdvance(Consumer<? super X> action) {
				while (this.running) {
					if (!currentBuffer.isEmpty()) {
						action.accept(currentBuffer.remove(0));
						return true;
					} else {
						try {
							List<X> newResults = supplier.get();
							if (newResults != null) {
								currentBuffer.addAll(newResults);
							}
						} catch (Exception e) {
							errorHandler.accept(e);
						}
					}	
				}
					
				return false;
			}

			@Override
			public long estimateSize() {
				if (this.running) {
					return -1;
				} else {
					return 0;
				}
			}


		};
	}
}
