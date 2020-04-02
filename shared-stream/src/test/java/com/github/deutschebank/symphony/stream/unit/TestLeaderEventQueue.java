package com.github.deutschebank.symphony.stream.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import com.github.deutschebank.symphony.stream.fixture.BasicLeaderOnlyQueue;
import com.github.deutschebank.symphony.stream.fixture.QueueItem;
import com.github.deutschebank.symphony.stream.lq.AbstractLeaderOnlyQueue.Alert;

public class TestLeaderEventQueue {

	@Test
	public void testSingleQueueAlert() {	
		List<QueueItem> collected = new ArrayList<QueueItem>();
		List<QueueItem> expected = new ArrayList<QueueItem>();
		Consumer<QueueItem> ci = x -> collected.add(x);
		int[] count = { 0 };
		Alert a = new Alert() {
			
			public void queueTooFull() {
				count[0] ++;
			}
		};
		
		
		BasicLeaderOnlyQueue queue = new BasicLeaderOnlyQueue(Collections.singletonList(ci), x -> x.printStackTrace(), a) {
			@Override
			protected long getMaxAge() {
				return 70;	//ms
			}
		};
		
		for (int i = 0; i < 100; i++) {
			QueueItem qi = new QueueItem("item"+i, System.currentTimeMillis());
			queue.accept(qi);
			expected.add(qi);
			tinySleep();
		}
		
		// should be lots of alerts, but no actual processing
		int size = collected.size();
		Assert.assertEquals(0, size);
		Assert.assertTrue(count[0] > 10);
		Assert.assertTrue(count[0] < 90);
		
		// try removing some from the queue
		queue.remove(expected.get(44).id);
		
		// now make it the leader
		queue.makeLeader();
		
		// everything should flow through
		Assert.assertEquals(99, collected.size());
		
		// except the one we removed
		Assert.assertEquals(0, collected.stream().filter(qi -> qi.id.equals("item44")).count());
		

	}

	private void tinySleep() {
		try {
			Thread.sleep(new Random().nextInt(6));
		} catch (InterruptedException e) {
		}
	}
	
}
