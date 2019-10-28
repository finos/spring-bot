package com.github.deutschebank.symphony.spring.api;

import javax.net.ssl.TrustManager;

import com.github.deutschebank.symphony.spring.api.properties.PodProperties;
import com.symphony.api.id.SymphonyIdentity;

public interface ApiInstanceFactory {

	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception;
	
}
