package org.finos.symphony.toolkit.spring.api.factories;

import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.id.SymphonyIdentity;

public class BasicAPIInstance implements ApiInstance {
	
	protected final ApiBuilder sessionAuthApiBuilder;
	protected final ApiBuilder keyAuthApiBuilder;
	protected final ApiBuilder relayApiBuilder;
	protected final ApiBuilder loginApiBuilder;
	protected final ApiBuilder podApiBuilder;
	protected final ApiBuilder agentApiBuilder;
	protected final SymphonyIdentity identity;
	
	public BasicAPIInstance(ApiBuilder sessionAuthApiBuilder, ApiBuilder keyAuthApiBuilder,
			ApiBuilder relayApiBuilder, ApiBuilder loginApiBuilder, ApiBuilder podApiBuilder,
			ApiBuilder agentApiBuilder, SymphonyIdentity identity) {
		super();
		this.sessionAuthApiBuilder = sessionAuthApiBuilder;
		this.keyAuthApiBuilder = keyAuthApiBuilder;
		this.relayApiBuilder = relayApiBuilder;
		this.loginApiBuilder = loginApiBuilder;
		this.podApiBuilder = podApiBuilder;
		this.agentApiBuilder = agentApiBuilder;
		this.identity = identity;
	}

	@Override
	public <X> X getPodApi(Class<X> c) {
		return podApiBuilder.getApi(c);
	}

	@Override
	public <X> X getAgentApi(Class<X> c) {
		return agentApiBuilder.getApi(c);
	}

	@Override
	public <X> X getSessionAuthApi(Class<X> c) {
		return sessionAuthApiBuilder.getApi(c);
	}

	@Override
	public <X> X getKeyAuthApi(Class<X> c) {
		return keyAuthApiBuilder.getApi(c);
	}

	@Override
	public <X> X getRelayApi(Class<X> c) {
		return relayApiBuilder.getApi(c);
	}

	@Override
	public <X> X getLoginApi(Class<X> c) {
		return loginApiBuilder.getApi(c);
	}

	@Override
	public SymphonyIdentity getIdentity() {
		return identity;
	}
}