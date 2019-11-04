package com.github.deutschebank.symphony.spring.api;

import javax.net.ssl.TrustManager;

import com.github.deutschebank.symphony.spring.api.properties.PodProperties;
import com.symphony.api.id.SymphonyIdentity;

/**
 * Interface providing an {@link ApiInstance}.  Provides the {@link ApiInstance} for a single Symphony pod.
 * 
 * @author robmoffat
 *
 */
public interface ApiInstanceFactory {

	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception;
	
}
