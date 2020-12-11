package org.finos.symphony.toolkit.spring.app.jwt;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.finos.symphony.toolkit.spring.app.id.BCCertificateTools;
import org.finos.symphony.toolkit.spring.app.id.CertificateTools;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.bindings.AbstractApiBuilder;
import com.symphony.api.bindings.JWTHelper;
import com.symphony.api.model.PodCertificate;
import com.symphony.api.pod.PodApi;

public abstract class AbstractUserDetailsControllerTest extends AbstractTest {

	@Autowired
	protected MockMvc mockMvc;
	@MockBean
	ApiBuilderFactory abf;
	@MockBean(name = "somePodApi")
	PodApi podApi;
	CertificateTools tools = new BCCertificateTools();
	@Autowired
	ObjectMapper m;

	public AbstractUserDetailsControllerTest() {
		super();
	}

	@Test
	public void testGoodJWT() throws Exception {
		String token = asString(UserDetailsControllerWithConfiguredPodTest.class.getResourceAsStream("/sampleJwt.json"));
		long nowSecs = System.currentTimeMillis()/1000;
		nowSecs += 24*60*60;	// one day in future
		token = token.replace("1563205665", ""+nowSecs);
		testWithToken(token)	    	
			.andExpect(status().isOk())
	       	.andExpect(jsonPath("$.principal", containsString("blardy-blah@example.com")))
	    	.andExpect(jsonPath("$.details.firstName", containsString("Robert")));

	}
	
	@Test
	public void testExpiredJWT() throws Exception {
		String token = asString(UserDetailsControllerWithConfiguredPodTest.class.getResourceAsStream("/sampleJwt.json"));
		testWithToken(token).andExpect(status().is4xxClientError());
	}

	private ResultActions testWithToken(String token) throws NoSuchAlgorithmException, Exception, CertificateEncodingException {
		KeyPair keyPair = tools.createKeyPair();
		X509Certificate cert = tools.createSelfSignedCertificate("someId", keyPair);
		String certPem = Base64.getEncoder().encodeToString(cert.getEncoded());
		String jwt = JWTHelper.createSignedJwtFromClaims(token, keyPair.getPrivate());
		System.out.println("Public Key: "+Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
		
		// check that jwt is valid
		JwtHelper.decodeAndVerify(jwt, new RsaVerifier((RSAPublicKey) keyPair.getPublic(), "SHA512withRSA"));
		
		Mockito.when(abf.getObject()).thenReturn(new AbstractApiBuilder() {
			
			@SuppressWarnings("unchecked")
			@Override
			public <X> X getApi(Class<X> c) {
				Assert.assertEquals("https://your.agent.domain:443/pod", this.url);
				Assert.assertEquals(PodApi.class, c);
				return (X) podApi;
			}
		});
	
		Mockito.when(podApi.v1PodcertGet()).thenReturn(new PodCertificate().certificate(certPem));
		
	    return this.mockMvc.perform(
	    	get("/symphony-app/userDetails")
	    	.header("Authorization", "Bearer "+jwt)
	    	.contentType(MediaType.APPLICATION_JSON))
	    	.andDo(print());
	}

}