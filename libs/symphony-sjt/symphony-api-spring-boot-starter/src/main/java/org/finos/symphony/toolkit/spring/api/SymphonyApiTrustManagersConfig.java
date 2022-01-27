package org.finos.symphony.toolkit.spring.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.TrustManagerFactory;

import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.spring.api.properties.TrustStoreProperties;
import org.finos.symphony.toolkit.spring.api.properties.TrustStoreProperties.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.PemSymphonyIdentity;

@Configuration
@EnableConfigurationProperties(SymphonyApiProperties.class)
public class SymphonyApiTrustManagersConfig {

	public static final String SYMPHONY_TRUST_MANAGERS_BEAN = "symphonyTrustManagers";
	
	@Autowired
	SymphonyApiProperties symphonyProperties;
	
	@Autowired
	ResourceLoader resourceLoader; 
	
	@Autowired
	ObjectMapper mapper;

	@Bean(name=SYMPHONY_TRUST_MANAGERS_BEAN)
	@ConditionalOnMissingBean(name=SYMPHONY_TRUST_MANAGERS_BEAN)
	public TrustManagerFactory symphonyTrustManagerFactory() throws Exception {
		if (symphonyProperties.getTrustStore() != null) {
			TrustStoreProperties tsp = symphonyProperties.getTrustStore();
			
			// input stream of trusted certs
			InputStream is = tsp.getType() == Type.INLINE_PEMS ? 
					new ByteArrayInputStream(tsp.getInlinePems().getBytes()) :
					resourceLoader.getResource(tsp.getLocation()).getInputStream();
					
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore keystore;
			
			if ((tsp.getType() == Type.PEMS) || (tsp.getType() == Type.INLINE_PEMS)) {
				keystore = KeyStore.getInstance("PKCS12");
				keystore.load(null, null);
				Scanner scanner = new Scanner(is);
				scanner.useDelimiter("-----END CERTIFICATE-----");
				int id = 0;
				while (scanner.hasNext()) {
					String certstring = scanner.next();
					if (certstring.length()>5) {
						X509Certificate cert = PemSymphonyIdentity.createCertificate(certstring);
						keystore.setCertificateEntry("cert-"+(id++), cert);
					}				
				}
				scanner.close();
			} else {
				String storeType = tsp.getType().name();
				keystore = KeyStore.getInstance(storeType);
				keystore.load(is, tsp.getPassword().toCharArray());
			}
	
			tmf.init(keystore);
			return tmf;
			
		} else {
			return null;
		}
	}

}
