package org.finos.symphony.toolkit.spring.api.trust;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.id.PemSymphonyIdentity;
import com.symphony.api.id.testing.StreamHelp;

@ExtendWith(SpringExtension.class)

@SpringBootTest(
		classes={TestApplication.class})
@ActiveProfiles({"inline", "p12idtest", "develop"})
public class InlinePemTrustStoreConfigTest {

	@Autowired
	TrustManagerFactory tmf;
	
	/**
	 * Checks instantiation of trust managers
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void checkInlinePemCertificatesWork() throws Exception {
		TrustManager[]  tm = tmf.getTrustManagers();
		Assertions.assertEquals(1, tm.length);
		X509TrustManager t = (X509TrustManager) tm[0];

		Map<String, Object> id = StreamHelp.getProperties("symphony-develop-bot4-identity", Map.class);
		X509Certificate cert = PemSymphonyIdentity.createCertificate((String) ((ArrayList)id.get("chain")).get(0));
		
		t.checkClientTrusted(new X509Certificate[] { cert }, "RSA");

	}
}
