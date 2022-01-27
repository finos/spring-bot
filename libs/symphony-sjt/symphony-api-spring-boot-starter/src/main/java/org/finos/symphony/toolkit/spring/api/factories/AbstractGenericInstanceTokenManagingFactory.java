package org.finos.symphony.toolkit.spring.api.factories;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;

import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.TokenManager;

public abstract class AbstractGenericInstanceTokenManagingFactory<ID> extends AbstractGenericInstanceFactory<ID>{

	public AbstractGenericInstanceTokenManagingFactory(ApiBuilderFactory apiBuilderFactory) {
		super(apiBuilderFactory);
	}

	@Override
	public ApiInstance createApiInstance(ID id, PodProperties pp, TrustManager[] trustManagers) throws Exception {
		ApiBuilder sessionAuthApiBuilder = createApiBuilder(pp, pp.getSessionAuth(), id, trustManagers, "sessionAuth");
		ApiBuilder keyAuthApiBuilder = createApiBuilder(pp, pp.getKeyAuth(), id, trustManagers, "keyAuth");
		ApiBuilder relayApiBuilder = createApiBuilder(pp, pp.getRelay(), id, trustManagers, "relay");
		ApiBuilder loginApiBuilder = createApiBuilder(pp, pp.getLogin(), id, trustManagers, "login");
		
		TokenManager tm = createTokenManager(id, pp, sessionAuthApiBuilder, keyAuthApiBuilder, relayApiBuilder, loginApiBuilder);
	
		ApiBuilder podApiBuilder = createApiBuilder(pp, pp.getPod(), id, trustManagers, "pod", tm);
		ApiBuilder agentApiBuilder = createApiBuilder(pp, pp.getAgent(), id, trustManagers, "agent", tm);
				
		ApiInstance out = new BasicAPIInstance(
				sessionAuthApiBuilder, 
				keyAuthApiBuilder,
				relayApiBuilder,
				loginApiBuilder,
				podApiBuilder,
				agentApiBuilder,
				getIdentity(id));
		
		return out;
				
	}
	
	protected abstract TokenManager createTokenManager(ID id, PodProperties pp, ApiBuilder sessionAuth, ApiBuilder keyAuth, ApiBuilder relay, ApiBuilder login);
}
