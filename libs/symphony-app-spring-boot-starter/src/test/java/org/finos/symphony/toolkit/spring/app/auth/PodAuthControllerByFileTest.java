package org.finos.symphony.toolkit.spring.app.auth;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.AbstractApiBuilder;
import com.symphony.api.model.AppAuthenticateRequest;
import com.symphony.api.model.ExtensionAppAuthenticateRequest;
import com.symphony.api.model.ExtensionAppTokens;


/**
 * Loads the details of the pod up from a file.
 *   
 * 
 * @author Rob Moffat
 *
 */
@TestPropertySource(properties={
		"symphony.app.store.location=src/test/resources/pods",
		"symphony.app.proxy.host=myproxy.com",
		"symphony.app.circleOfTrust=FULL"
})
public class PodAuthControllerByFileTest extends AbstractTest {

	@MockBean
	ApiBuilderFactory abf;
	
	@MockBean(name="someNewBean")
	CertificateAuthenticationApi authApi;

	
	@Test
	public void testAuthentication() throws Exception {
		
		Mockito.when(abf.getObject()).thenReturn(new AbstractApiBuilder() {
			
			@SuppressWarnings("unchecked")
			@Override
			public <X> X getApi(Class<X> c) {
				Assertions.assertEquals("https://your.pod.domain:8444/sessionauth", this.url);
				Assertions.assertEquals(CertificateAuthenticationApi.class, c);
				Assertions.assertEquals("myproxy.com", this.proxyHost);
				return (X) authApi;
			}
		});
		
		
		Mockito.when(authApi.v1AuthenticateExtensionAppPost(Mockito.any())).thenAnswer(i -> {
			ExtensionAppAuthenticateRequest ar = (ExtensionAppAuthenticateRequest) i.getArgument(0);
			return new ExtensionAppTokens().appId("appid123").appToken(ar.getAppToken()).symphonyToken("Sym123");
		});
		
		
        this.mockMvc.perform(
        	get("/symphony-app/podAuth?podId=9999")
        	.contentType(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(jsonPath("$.appId", containsString("appid123")))
    		.andExpect(jsonPath("$.tokenA", containsString("someAppId/")))
    		.andExpect(status().isOk());
    }
	
	@Test
	public void testNoPodFoundAuthentication() throws Exception {
		
		this.mockMvc.perform(
	        	get("/symphony-app/podAuth?podId=8473")
	        	.contentType(MediaType.APPLICATION_JSON))
	    		.andExpect(status().is4xxClientError())
	        	.andDo(print());
	}
		
}
