package org.finos.symphony.toolkit.spring.api.factories;

import com.symphony.api.id.SymphonyIdentity;

public class ApiInstanceDelegate implements ApiInstance {
	
	protected ApiInstance delegate;

	public ApiInstanceDelegate(ApiInstance delegate) {
		super();
		this.delegate = delegate;
	}

	public <X> X getPodApi(Class<X> c) {
		return delegate.getPodApi(c);
	}

	public <X> X getAgentApi(Class<X> c) {
		return delegate.getAgentApi(c);
	}

	public <X> X getSessionAuthApi(Class<X> c) {
		return delegate.getSessionAuthApi(c);
	}

	public <X> X getKeyAuthApi(Class<X> c) {
		return delegate.getKeyAuthApi(c);
	}

	public <X> X getRelayApi(Class<X> c) {
		return delegate.getRelayApi(c);
	}

	public <X> X getLoginApi(Class<X> c) {
		return delegate.getLoginApi(c);
	}

	public SymphonyIdentity getIdentity() {
		return delegate.getIdentity();
	}
}