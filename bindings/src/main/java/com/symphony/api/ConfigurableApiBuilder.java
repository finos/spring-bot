package com.symphony.api;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

public interface ConfigurableApiBuilder extends ApiBuilder {

	void setUrl(String baseUrl);
	void setProxyDetails(String proxyHost, String user, String password, int port);
	void setTokenManager(TokenManager tokenManager);
	void setTrustManagers(TrustManager[] trustManagers);
	void setKeyManagers(KeyManager[] keyManagers);

	TokenManager getTokenManager();
	TrustManager[] getTrustManagers();
	KeyManager[] getKeyManagers();

}