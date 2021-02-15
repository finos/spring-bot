package org.finos.symphony.toolkit.stream.cluster;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember.State;
import org.finos.symphony.toolkit.stream.fixture.Connectivity;
import org.finos.symphony.toolkit.stream.fixture.TestClusterMember;
import org.finos.symphony.toolkit.stream.fixture.TestNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Builds various sizes / speeds of network so we can test the clustering algorithm.
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractClusterTest {
	
	@BeforeEach
	public void setupLogLevel() {
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.WARN);
	}
	
	static class Setup {
		Connectivity c;
		TestNetwork n;
		List<ClusterMember> members;
		Set<Participant> allParticipants;
		int size = 9;
		int maxTimeout;
		
		public void shutdown() {
			for (ClusterMember m : members) {
				m.shutdown();
			}
		}

		public void startup() {
			for (ClusterMember m : members) {
				m.startup();
			}
		}
	}
	
	static class Configuration {
		
		public Configuration(int size, int time) {
			this.size = size;
			this.time = time;
		}

		int size;
		int time;

		@Override
		public String toString() {
			return "Configuration [size=" + size + ", time=" + time + "]";
		}

	}
	
	public Setup setupNetwork(Configuration c) {
		Setup setup = new Setup();
		setup.c = new Connectivity();
		setup.n = new TestNetwork(setup.c, c.time / 6);
		Random r= new Random();
		
		setup.allParticipants = IntStream.range(0, c.size).mapToObj(i -> new Participant("P"+i)).collect(Collectors.toSet());
		
		setup.members = setup.allParticipants.stream().map(p -> createClusterMember(p, c, setup, r))
			.collect(Collectors.toList());
		
		//setup.members.forEach(m -> setup.allParticipants.forEach(p -> m.accept(p)));
		
		setup.c.set(Collections.singleton(setup.allParticipants));
		setup.size = c.size;
		setup.maxTimeout = c.time;
		return setup;
	}

	protected abstract TestClusterMember createClusterMember(Participant p, Configuration c, Setup setup, Random r);

	protected long countLeaders(Setup s) {
		return s.members.stream().filter(m -> m.getState() == State.LEADER).count();
	}
	
	protected Set<Participant> getLeaders(Setup s) {
		return s.members.stream()
				.filter(m -> m.getState() == State.LEADER)
				.map(cm -> cm.getSelfDetails())
				.collect(Collectors.toSet());
	}
	
	protected void waitForLeaderCount(Setup s, int c) throws InterruptedException {
		Set<Participant> lastLeaders = null;
		Set<Participant> newLeaders = null;
		
		do {
			lastLeaders = newLeaders;
			newLeaders =  getLeaders(s);
			Thread.sleep(s.maxTimeout+10);
			if ((newLeaders.equals(lastLeaders)) && (newLeaders.size() == c)) {
				return;
			}
		} while (true);
	}

}
