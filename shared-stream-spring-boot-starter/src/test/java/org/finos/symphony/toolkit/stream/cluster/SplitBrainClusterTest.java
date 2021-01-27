package org.finos.symphony.toolkit.stream.cluster;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.finos.symphony.toolkit.stream.Participant;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SplitBrainClusterTest extends AbstractMajorityClusterTest {

	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testSplitBrainWithLeaderInSmallestSubset(Configuration c) throws InterruptedException {
		System.out.println("--------------------------------------");
		System.out.println("TEST: "+c.size+"  "+c.time+"ms");
		System.out.println("--------------------------------------");
			
		Setup s = setupNetwork(c);
		s.startup();
			
		waitForLeaderCount(s, 1);
		
		splitTheBrain(s, getLeaders(s).stream().findFirst().get());
		
		if (c.size > 1) {
		
			waitForLeaderCount(s, 2);
				
			getLeaders(s).stream().findFirst().orElseThrow(() -> new IllegalStateException("Should be a leader"));
			
			healTheBrain(s);
			
			waitForLeaderCount(s, 1);
	
			getLeaders(s).stream().findFirst().orElseThrow(() -> new IllegalStateException("Should be a leader"));

		}
		
		s.shutdown();

	}

	/**
	 * Leader should always end up in the smallest (b) group
	 */
	private void splitTheBrain(Setup s, Participant leader) throws InterruptedException {
		Set<Participant> a = new HashSet<Participant>();
		Set<Participant> b = new HashSet<Participant>();

		b.add(leader);
		
		for (Participant participant : s.allParticipants) {
			if (participant == leader) {
				// already done
			} else if (a.size() > b.size()) {
				b.add(participant);
			} else {
				a.add(participant);
			}
		}	
		
		
		Set<Set<Participant>> out = new HashSet<Set<Participant>>();
		out.add(a);
		out.add(b);
		System.out.println("Splitting: "+out);
		s.c.set(out);
		Thread.sleep(500);
	}

	private void healTheBrain(Setup s) throws InterruptedException {
		System.out.println("healing: "+s.allParticipants);
		s.c.set(Collections.singleton(s.allParticipants));
		Thread.sleep(500);
	}
}
