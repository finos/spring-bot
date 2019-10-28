package com.github.deutschebank.symphony.spring.app.auth;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import com.github.deutschebank.symphony.spring.api.builders.ApiBuilderFactory;
import com.github.deutschebank.symphony.spring.app.AbstractTest;
import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.bindings.AbstractApiBuilder;
import com.symphony.api.model.AppAuthenticateRequest;
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
		"symphony.app.proxy.host=myproxy.com"
})
public class PodAuthControllerByFileTest extends AbstractTest {

	@MockBean
	ApiBuilderFactory abf;
	
	@MockBean(name="someNewBean")
	AuthenticationApi authApi;

	
	@Test
	public void testAuthentication() throws Exception {
		
		Mockito.when(abf.getObject()).thenReturn(new AbstractApiBuilder() {
			
			@SuppressWarnings("unchecked")
			@Override
			public <X> X getApi(Class<X> c) {
				Assert.assertEquals("https://your.pod.domain:8444/sessionauth", this.url);
				Assert.assertEquals(AuthenticationApi.class, c);
				Assert.assertEquals("myproxy.com", this.proxyHost);
				return (X) authApi;
			}
		});
		
		
		Mockito.when(authApi.v1AuthenticateExtensionAppPost(Mockito.any())).thenAnswer(i -> {
			AppAuthenticateRequest ar = (AppAuthenticateRequest) i.getArgument(0);
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
