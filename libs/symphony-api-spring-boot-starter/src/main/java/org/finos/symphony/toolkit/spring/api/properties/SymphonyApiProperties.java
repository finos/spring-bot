package org.finos.symphony.toolkit.spring.api.properties;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony")
public class SymphonyApiProperties {

	private List<PodProperties> apis;
	private TrustStoreProperties trustStore;
	private DefaultConfigProperties config;
	
	@PostConstruct
	public void init() {
		if(Objects.isNull(config)) {
			config = new DefaultConfigProperties();
			config.setLocalPOD(Boolean.TRUE);
		} else if(Objects.isNull(config.isLocalPOD())) {
			config.setLocalPOD(Boolean.TRUE);
		}
	}

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

	/**
	 * @return the config
	 */
	public DefaultConfigProperties getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(DefaultConfigProperties config) {
		this.config = config;
	}

}
