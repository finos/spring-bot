package com.symphony.api.bindings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ProxyConnectionIT extends AbstractIT {

	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testConnectionToRandomUrl(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails(null, null, null, 8080);
		Assertions.assertTrue(b.testConnection("https://google.com"));
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testConnectionToSymphonyPod(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails(null, null, null, 8080);
		Assertions.assertTrue(b.testConnection(TestPodConfig.POD_URL));
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testConnectionToSymphonyRelay(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setConnectTimeout(6000);
		b.setProxyDetails(null, null, null, 8080);
		Assertions.assertTrue(b.testConnection(TestPodConfig.RELAY_URL));
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testConnectionToSomethingBroken(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails(null, null, null, 8080);
		Assertions.assertFalse(b.testConnection("https://idontexist.co.uk"));
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testWithBrokenProxy(TestClientStrategy tcs) {
		ConfigurableApiBuilder b = tcs.getApiBuilder();
		b.setProxyDetails("idontexist.co.uk", null, null, 8080);
		Assertions.assertFalse(b.testConnection(TestPodConfig.POD_URL));
	}
}
