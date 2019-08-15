package com.symphony;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public abstract class AbstractTest {
	
	public static final String ROOM = "y3EJYqKMwG7Jn7_YqyYdiX___pR3YrnTdA==";

//	@DataPoint
//	public static TestClientStrategy cxf = DBPodConfig.CXF_CERT;
//	
	@DataPoint
	public static TestClientStrategy jersey = DBPodConfig.JERSEY_RSA;

}
