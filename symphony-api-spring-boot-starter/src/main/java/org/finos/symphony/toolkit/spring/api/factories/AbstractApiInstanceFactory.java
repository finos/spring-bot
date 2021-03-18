package org.finos.symphony.toolkit.spring.api.factories;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;

public abstract class AbstractApiInstanceFactory implements ApiInstanceFactory {

	protected ApiBuilderFactory apiBuilderFactory;

	public AbstractApiInstanceFactory(ApiBuilderFactory apiBuilderFactory) {
		super();
		this.apiBuilderFactory = apiBuilderFactory;
	}

}