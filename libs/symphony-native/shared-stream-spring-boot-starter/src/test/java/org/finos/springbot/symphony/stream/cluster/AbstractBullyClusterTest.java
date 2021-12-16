package org.finos.springbot.symphony.stream.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.symphony.stream.fixture.TestClusterMember;

public abstract class AbstractBullyClusterTest extends AbstractClusterTest {

	private static final int LONGEST_TIMEOUT = 300;

	public static Stream<Configuration> setupConfigurations() {
		List<Configuration> out = new ArrayList<>();
		for (int i = 7; i >= 1; i=i-2) {
			for (int j = 100; j < LONGEST_TIMEOUT; j=j+100) {
				out.add(new Configuration(i, j));
			}
		}
		
		return out.stream();
	}

	@Override
	protected TestClusterMember createClusterMember(Participant p, Configuration c, Setup setup, Random r) {
		return new TestClusterMember(
				p, 
				c.time + r.nextInt(c.time), 
				setup.n, 
				() -> setup.canTalkToSymphony.contains(p),
				setup.ls);
	}

}
