package org.finos.springbot.symphony.stream.cluster;

import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.symphony.stream.fixture.DummyLeaderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BullyDeathClusterTest extends AbstractBullyClusterTest {

	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testClusterLeadership(Configuration c) throws InterruptedException {
		System.out.println("--------------------------------------");
		System.out.println("TEST: "+c.size+"  "+c.time+"ms");
		System.out.println("--------------------------------------");
		
		Setup s = setupNetwork(c);
		s.startup();
		
		waitForLeaderCount(s);
		
		if (c.size > 1) {

			// kill the old leader, get a new one
			Participant oldLeader = getLeaders(s).stream().findFirst().orElseThrow(() -> new IllegalStateException("Should be a leader"));
			System.out.println("--------------------------------------");
			s.c.isolate(oldLeader);
			s.canTalkToSymphony.remove(oldLeader);

		
			waitForLeaderCount(s);
	
			getLeaders(s).stream()
				.filter(l -> !l.equals(oldLeader))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Should be a new leader"));
					
			// when we heal the network, we should go back to a single leader
			System.out.println("--------------------------------------");
			s.c.connect(oldLeader);
			waitForLeaderCount(s);
			getLeaders(s).stream().findFirst().orElseThrow(() -> new IllegalStateException("Should be a leader"));
		
		}
		
		s.shutdown();
		
		Assertions.assertTrue(((DummyLeaderServiceImpl) s.ls).leaderHistory.size() < 10);
	}
}
