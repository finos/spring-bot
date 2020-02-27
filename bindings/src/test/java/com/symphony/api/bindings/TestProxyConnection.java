package com.symphony.api.bindings;

import org.junit.Assert;
import org.junit.experimental.theories.Theory;

public class TestProxyConnection extends AbstractTest {

	@Theory
	public void testConnection(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		Assert.assertTrue(b.testConnection("https://google.com"));
	}
}
