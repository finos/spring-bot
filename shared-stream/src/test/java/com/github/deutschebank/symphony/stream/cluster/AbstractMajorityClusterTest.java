package com.github.deutschebank.symphony.stream.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.experimental.theories.DataPoints;

import com.github.deutschebank.symphony.stream.fixture.TestingClusterMember;
import com.github.deutschebank.symphony.stream.msg.Participant;

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
	protected TestingClusterMember createClusterMember(Participant p, Configuration c, Setup setup, Random r) {
		return new TestingClusterMember(
				"Member "+p.toString(), 
				p, 
				c.time + r.nextInt(c.time), 
				setup.n, 
				c.size);
	}

	public AbstractMajorityClusterTest() {
		super();
	}

}