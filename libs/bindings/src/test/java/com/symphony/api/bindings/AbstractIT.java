package com.symphony.api.bindings;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class AbstractIT {
	
	// A room on Symphony called "DB Symphony Practice Bots Integration Test"
	// on the develop pod.
	public static final String ROOM = "y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA==";

//	certificates not working on develop pod.	
//	@DataPoint
//	public static TestClientStrategy cxf = TestPodConfig.CXF_CERT;
//	
	public static TestClientStrategy jersey = TestPodConfig.JERSEY_RSA;

	public static TestClientStrategy cxf = TestPodConfig.CXF_RSA;
	
	public static Stream<TestClientStrategy> setupConfigurations() {
		return Arrays.asList(jersey, cxf).stream();
	} 

}
