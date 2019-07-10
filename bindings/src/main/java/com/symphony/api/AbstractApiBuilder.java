package com.symphony.api;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

public abstract class AbstractApiBuilder implements ConfigurableApiBuilder {

	public String url;
	protected KeyManager[] keyManagers = null;
	protected TrustManager[] trustManagers = null;
	
	public AbstractApiBuilder() {
	}
	
	public AbstractApiBuilder(String url) {
		this.url = url;
	}
	
	public AbstractApiBuilder(String url, KeyManager[] keyManagers) {
		this.url = url;
		this.keyManagers = keyManagers;
	}
	
	protected String proxyHost;
	protected String user;
	protected String password;
	protected int port;
	protected TokenManager tokenManager;
	
	@Override
	public void setProxyDetails(String proxyHost, String user, String password, int port) {
		this.proxyHost = proxyHost;
		this.user= user;
		this.port = port;
		this.password = password;
	}

	@Override
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	@Override
	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}
	
	@Override
	public TrustManager[] getTrustManagers() {
		return trustManagers;
	}

	@Override
	public void setTrustManagers(TrustManager[] trustManagers) {
		this.trustManagers = trustManagers;
	}

	@Override
	public KeyManager[] getKeyManagers() {
		return keyManagers;
	}

	@Override
	public void setKeyManagers(KeyManager[] keyManagers) {
		this.keyManagers = keyManagers;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
