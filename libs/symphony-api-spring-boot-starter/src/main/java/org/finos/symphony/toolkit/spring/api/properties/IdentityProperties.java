package org.finos.symphony.toolkit.spring.api.properties;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.P12SymphonyIdentity;
import com.symphony.api.id.PemSymphonyIdentity;
import com.symphony.api.id.SymphonyIdentity;

@ConfigurationProperties
public class IdentityProperties {

	public static enum Type { JSON, PKCS12, PEM }
	
	private String email;
	private String commonName;
	private String location;
	private String password;
	private String privateKey;
	private List<String> certificates;
	private Type type;
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public List<String> getCertificates() {
		return certificates;
	}
	public void setCertificates(List<String> certificates) {
		this.certificates = certificates;
	}
	
	@SuppressWarnings("deprecation")
	public static SymphonyIdentity instantiateIdentityFromDetails(ResourceLoader loader, IdentityProperties details, ObjectMapper om) throws IOException {
		if (!StringUtils.isEmpty(details.getLocation())) {
			Resource r = loader.getResource(details.getLocation());
			boolean pkcs12 = (details.getType() == Type.PKCS12) || (details.getType() == null) && (details.getLocation().endsWith("p12"));
			boolean json =  (details.getType() == Type.JSON) || (details.getType() == null) && (details.getLocation().endsWith("json"));
			if (r.isReadable()) {
				if (pkcs12) {
					P12SymphonyIdentity id = new P12SymphonyIdentity(r.getInputStream(), details.getPassword(), details.getEmail());
					return id;
				} else if (json) {
					SymphonyIdentity id = om.readValue(r.getInputStream(), SymphonyIdentity.class);
					return id;
				}
			}
		} 
		
		if (!StringUtils.isEmpty(details.getPrivateKey())) {
			if ((details.getCertificates() != null) && (details.getCertificates().size() > 0)) {
				String[] certs = (String[]) details.getCertificates().toArray(new String[details.getCertificates().size()]);
				PemSymphonyIdentity id = new PemSymphonyIdentity(details.getPrivateKey(), certs, details.getEmail());
				return id;
			} else {
				PemSymphonyIdentity id = new PemSymphonyIdentity(details.getPrivateKey(), details.getCommonName(), details.getEmail());
				return id;
			}
			
		} 
		
		return null;
	}
	
}
