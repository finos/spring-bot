package com.symphony.api.bindings;

import org.junit.Assert;
import org.junit.experimental.theories.Theory;

public class TestMultiProxy extends AbstractTest {

	@Theory
	public void testProxyConnectionChecker(TestClientStrategy s) throws Exception {
		ConfigurableApiBuilder cab = s.getAPiBuilder();
		Assert.assertTrue(cab.testConnection("https://google.com"));
	}
}
