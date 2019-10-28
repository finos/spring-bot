package com.symphony.spring.api;

import javax.net.ssl.TrustManager;

import com.symphony.id.SymphonyIdentity;
import com.symphony.spring.api.properties.PodProperties;

public interface ApiInstanceFactory {

	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception;
	
}
