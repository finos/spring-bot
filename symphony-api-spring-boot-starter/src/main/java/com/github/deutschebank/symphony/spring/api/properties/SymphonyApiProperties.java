package com.github.deutschebank.symphony.spring.api.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony")
public class SymphonyApiProperties {

	List<PodProperties> apis;
	TrustStoreProperties trustStore;

	public TrustStoreProperties getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(TrustStoreProperties trustStore) {
		this.trustStore = trustStore;
	}

	public List<PodProperties> getApis() {
		return apis;
	}

	public void setApis(List<PodProperties> apis) {
		this.apis = apis;
	}
		
}

