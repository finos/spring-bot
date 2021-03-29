package org.finos.symphony.toolkit.spring.app.obo;

import java.security.Principal;

import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.spring.api.factories.GenericInstanceFactory;

/**
 * Interface providing an {@link ApiInstance} for use with OBO.  This is for occasions where an
 * app is working "on-behalf-of" another user.  As with {@link ApiInstance}, the identity of the 
 * user, tokens, health and metrics are all handled by the factory.
 * 
 * @author robmoffat
 *
 */
public interface OboInstanceFactory extends GenericInstanceFactory<OboIdentity> {

	/**
	 * Creates an OBO-based ApiInstance, for a given pod, using autowired {@link TrustManagerFactory} bean.
	 */
	public ApiInstance createApiInstance(Long oboUserId, String companyId) throws Exception;
	
	/**
	 * Creates an OBO-based ApiInstance, for the default pod (assuming one is set), using autowired {@link TrustManagerFactory} bean.
	 */
	public ApiInstance createApiInstance(Long oboUserId) throws Exception;
	
	/**
	 * Creates an OBO-based ApiInstance, extracting the oboUserId and companyId from the principal.  This should be used when authenticating via
	 * Symphony's JWT tokens.
	 */
	public ApiInstance createApiInstance(Principal p) throws Exception;

}
