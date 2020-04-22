package com.symphony.api.bindings;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

public interface ConfigurableApiBuilder extends ApiBuilder {

	void setUrl(String baseUrl);
	void setProxyDetails(String proxyHost, String user, String password, int port);
	void setWrappers(ApiWrapper[] wrappers);
	void setTrustManagers(TrustManager[] trustManagers);
	void setKeyManagers(KeyManager[] keyManagers);

	ApiWrapper[] getWrappers();
	TrustManager[] getTrustManagers();
	KeyManager[] getKeyManagers();
	
	/**
	 * Attempts to make a connection with the given settings.  
	 * This allows you to try out different alternative proxy options.
	 */
	boolean testConnection(String url);

	void setConnectTimeout(long ct);

}