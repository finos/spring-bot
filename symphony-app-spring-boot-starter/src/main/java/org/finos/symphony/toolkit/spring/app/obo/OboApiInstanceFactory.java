package org.finos.symphony.toolkit.spring.app.obo;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;

/**
 * Interface providing an {@link ApiInstance}.  This is for occasions where an
 * app is working "on-behalf-of" another user.
 * 
 * @author robmoffat
 *
 */
public interface OboApiInstanceFactory {

	/**
	 * Creates an OBO-based ApiInstance, for a given pod, using given trust managers
	 */
	public ApiInstance createApiInstance(Long oboUserId, PodProperties pp, TrustManager[] trustManagers) throws Exception;

	/**
	 * Creates an OBO-based ApiInstance, for a given pod, using autowired {@link TrustManagerFactory} bean.
	 */
	public ApiInstance createApiInstance(Long oboUserId, PodProperties pp) throws Exception;
	
	/**
	 * Creates an OBO-based ApiInstance, for the default pod (assuming one is set), using autowired {@link TrustManagerFactory} bean.
	 */
	public ApiInstance createApiInstance(Long oboUserId) throws Exception;

}
