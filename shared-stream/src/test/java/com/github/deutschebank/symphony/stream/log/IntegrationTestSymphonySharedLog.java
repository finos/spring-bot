package com.github.deutschebank.symphony.stream.log;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.github.deutschebank.symphony.stream.AbstractIntegrationTest;
import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.log.SymphonyRoomSharedLog;

public class IntegrationTestSymphonySharedLog extends AbstractIntegrationTest {

	private String streamId = "y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA==";
	
	@Test
	public void testParticipantWrite() {
		SymphonyRoomSharedLog ssl = new SymphonyRoomSharedLog(streamId, messagesApi, "test");
		
		Participant p1 = new Participant("testing-participant-"+new Random(122).nextLong());
		Participant p2 = new Participant("testing-participant-"+new Random(122).nextLong());
		ssl.writeParticipantMessage(p1);
		ssl.writeParticipantMessage(p2);
		
		List<Participant> returned = ssl.getRegisteredParticipants(p1);
		Assert.assertTrue(returned.contains(p1));
		Assert.assertTrue(returned.contains(p2));
	}
	
	@Test
	public void testLeaderWrite() {
		SymphonyRoomSharedLog ssl = new SymphonyRoomSharedLog(streamId, messagesApi, "test");
		
		Participant p1 = new Participant("testing-participant-"+new Random(122).nextLong());
		Participant p2 = new Participant("testing-participant-"+new Random(122).nextLong());
		ssl.writeLeaderMessage(p1);
		ssl.writeLeaderMessage(p2);
		for (int i = 0; i < 30; i++) {
			// sometimes takes a few tries to get the thing back
			Optional<Participant> returned = ssl.getLeader(p1);
			if (returned.isPresent()) {
				Assert.assertEquals(p2, returned.get());
				return;
			}
		}
		
		Assert.fail("Didn't find leader");
	}
	
	
}
