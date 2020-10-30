package com.symphony.api.bindings;

import org.junit.Assert;
import org.junit.experimental.theories.Theory;

public class ProxyConnectionIT extends AbstractTest {

	@Theory
	public void testConnectionToRandomUrl(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails(null, null, null, 8080);
		Assert.assertTrue(b.testConnection("https://google.com"));
	}
	
	@Theory
	public void testConnectionToSymphonyPod(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails(null, null, null, 8080);
		Assert.assertTrue(b.testConnection(TestPodConfig.POD_URL));
	}
	
	@Theory
	public void testConnectionToSymphonyRelay(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setConnectTimeout(6000);
		b.setProxyDetails(null, null, null, 8080);
		Assert.assertTrue(b.testConnection(TestPodConfig.RELAY_URL));
	}
	
	@Theory
	public void testConnectionToSomethingBroken(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails(null, null, null, 8080);
		Assert.assertFalse(b.testConnection("https://idontexist.co.uk"));
	}
	
	@Theory
	public void testWithBrokenProxy(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails("idontexist.co.uk", null, null, 8080);
		Assert.assertFalse(b.testConnection(TestPodConfig.POD_URL));
	}
}
