package org.finos.symphony.toolkit.stream.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.voting.BullyDecider;
import org.finos.symphony.toolkit.stream.fixture.TestClusterMember;

public abstract class AbstractBullyClusterTest extends AbstractClusterTest {

	private static final int LONGEST_TIMEOUT = 250;

	public static Stream<Configuration> setupConfigurations() {
		List<Configuration> out = new ArrayList<>();
		for (int i = 1; i <= 7; i=i+1) {
			for (int j = 100; j < LONGEST_TIMEOUT; j=j+50) {
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
				new BullyDecider(p));
	}

}
