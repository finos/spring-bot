package org.finos.symphony.toolkit.stream.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.voting.MajorityDecider;
import org.finos.symphony.toolkit.stream.fixture.TestClusterMember;
import org.junit.experimental.theories.DataPoints;

public abstract class AbstractMajorityClusterTest extends AbstractClusterTest {

	private static final int LONGEST_TIMEOUT = 250;

	@DataPoints
	public static Configuration[] setupConfigurations() {
		List<Configuration> out = new ArrayList<>();
		for (int i = 1; i <= 7; i=i+2) {
			for (int j = 100; j < LONGEST_TIMEOUT; j=j+50) {
				out.add(new Configuration(i, j));
			}
		}
		
		return (Configuration[]) out.toArray(new Configuration[out.size()]);
	}

	@Override
	protected TestClusterMember createClusterMember(Participant p, Configuration c, Setup setup, Random r) {
		return new TestClusterMember(
				p, 
				c.time + r.nextInt(c.time), 
				setup.n, 
				new MajorityDecider(() -> c.size, p));
	}

	public AbstractMajorityClusterTest() {
		super();
	}

}