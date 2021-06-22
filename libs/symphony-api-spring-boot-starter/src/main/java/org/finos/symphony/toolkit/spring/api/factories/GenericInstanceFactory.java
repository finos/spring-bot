package org.finos.symphony.toolkit.spring.api.factories;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.properties.PodProperties;

public interface GenericInstanceFactory<ID> {

	public ApiInstance createApiInstance(ID id, PodProperties pp, TrustManager[] trustManagers) throws Exception;
	
}
