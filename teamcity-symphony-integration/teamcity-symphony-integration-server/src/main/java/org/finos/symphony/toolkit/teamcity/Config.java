package org.finos.symphony.toolkit.teamcity;

import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.TrustStoreProperties;

public class Config {

	private PodProperties podProperties;
	private IdentityProperties identityProperties;
	private TrustStoreProperties trustStoreProperties;
	private String certificates;
	private String template;
	private String trustedPems;
	
	public PodProperties getPodProperties() {
		return podProperties;
	}
	public void setPodProperties(PodProperties podProperties) {
		this.podProperties = podProperties;
	}
	public IdentityProperties getIdentityProperties() {
		return identityProperties;
	}
	public void setIdentityProperties(IdentityProperties identityProperties) {
		this.identityProperties = identityProperties;
	}
	public TrustStoreProperties getTrustStoreProperties() {
		return trustStoreProperties;
	}
	public void setTrustStoreProperties(TrustStoreProperties trustStoreProperties) {
		this.trustStoreProperties = trustStoreProperties;
	}
	
	public String getCertificates() {
		return certificates;
	}
	public void setCertificates(String certificates) {
		this.certificates = certificates;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getTrustedPems() {
		return trustedPems;
	}
	public void setTrustedPems(String trustedPems) {
		this.trustedPems = trustedPems;
	}
	
}
