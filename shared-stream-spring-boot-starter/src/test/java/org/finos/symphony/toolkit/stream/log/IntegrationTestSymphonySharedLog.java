package org.finos.symphony.toolkit.stream.log;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.log.SymphonyRoomSharedLog;
import org.finos.symphony.toolkit.stream.spring.TestApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.agent.MessagesApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("develop")
public class IntegrationTestSymphonySharedLog  {

	private String streamId = "y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA==";
	
	@Autowired
	MessagesApi messagesApi;
	
	@Test
	public void testParticipantWrite() {
		SymphonyRoomSharedLog ssl = new SymphonyRoomSharedLog(streamId, messagesApi, "test", SymphonyRoomSharedLog.ONE_HOUR);
		
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
		SymphonyRoomSharedLog ssl = new SymphonyRoomSharedLog(streamId, messagesApi, "test", SymphonyRoomSharedLog.ONE_HOUR);
		
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
