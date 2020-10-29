package org.finos.symphony.toolkit.spring.api;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.properties.PodProperties;

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
