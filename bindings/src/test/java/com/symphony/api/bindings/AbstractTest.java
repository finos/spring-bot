package com.symphony.api.bindings;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public abstract class AbstractTest {
	
	// A room on Symphony called "DB Symphony Practice Bots Integration Test"
	// on the develop pod.
	public static final String ROOM = "y3EJYqKMwG7Jn7_YqyYdiX___pR3YrnTdA==";

//	certificates not working on develop pod.	
//	@DataPoint
//	public static TestClientStrategy cxf = TestPodConfig.CXF_CERT;
//	
	@DataPoint
	public static TestClientStrategy jersey = TestPodConfig.JERSEY_RSA;

	@DataPoint
	public static TestClientStrategy cxf = TestPodConfig.CXF_RSA;

}
