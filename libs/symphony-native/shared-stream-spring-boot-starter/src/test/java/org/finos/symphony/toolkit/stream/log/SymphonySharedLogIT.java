package org.finos.symphony.toolkit.stream.log;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.agent.MessagesApi;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes={TestApplication.class})
@ActiveProfiles("develop")
public class SymphonySharedLogIT  {

	// now using a room just for this purpose
	private String streamId = "QTG/xBPcpYtbMbfhGrjK7X///okjuFBXdA==";
	private String clusterName = "test";
	
	@Autowired
	MessagesApi messagesApi;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Test
	public void testParticipantWrite() {
		SymphonyRoomSharedLog ssl = new SymphonyRoomSharedLog(clusterName ,streamId, messagesApi, "test", SymphonyRoomSharedLog.ONE_HOUR, ejc);
		
		Participant p1 = new Participant("testing-participant-"+new Random(122).nextLong());
		Participant p2 = new Participant("testing-participant-"+new Random(122).nextLong());
		ssl.writeParticipantMessage(p1);
		ssl.writeParticipantMessage(p2);
		
		List<Participant> returned = ssl.getRecentParticipants();
		Assertions.assertTrue(returned.contains(p1));
		Assertions.assertTrue(returned.contains(p2));
	}
	
	@Test
	public void testLeaderWrite() {
		SymphonyRoomSharedLog ssl = new SymphonyRoomSharedLog(clusterName, streamId, messagesApi, "test", SymphonyRoomSharedLog.ONE_HOUR, ejc);
		
		Participant p1 = new Participant("testing-participant-"+new Random(122).nextLong());
		Participant p2 = new Participant("testing-participant-"+new Random(122).nextLong());
		ssl.writeLeaderMessage(p1);
		ssl.writeLeaderMessage(p2);
		for (int i = 0; i < 30; i++) {
			// sometimes takes a few tries to get the thing back
			Optional<Participant> returned = ssl.getLastRecordedLeader(p1);
			if (returned.isPresent()) {
				Assertions.assertEquals(p2, returned.get());
				return;
			}
		}
		
		Assertions.fail("Didn't find leader");
	}
	
	@Test
	public void testLocalLog() {
		LocalConsoleOnlyLog ssl = new LocalConsoleOnlyLog();
		
		Participant p1 = new Participant("testing-participant-"+new Random(122).nextLong());
		Participant p2 = new Participant("testing-participant-"+new Random(122).nextLong());
		ssl.writeParticipantMessage(p1);
		ssl.writeLeaderMessage(p2);
		
		List<Participant> returned = ssl.getRecentParticipants();
		Assertions.assertTrue(returned.isEmpty());
	}
	
	
}
