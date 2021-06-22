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
 * Pod Auth is where we look up the details of a pod, and make a request back to a pod.
 * Pods can be looked up from stored details, or from 
 *   
 * 
 * @author Rob Moffat
 *
 */
@TestPropertySource(properties={
	"symphony.apis.0.id=666",
	"symphony.apis.0.sessionauth.url=http://blah.com/sessionauth",
	"symphony.app.circleOfTrust=FULL"
})
public class PodAuthControllerByConfigTest extends AbstractTest {

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
				Assertions.assertEquals("http://blah.com/sessionauth", this.url);
				Assertions.assertEquals(CertificateAuthenticationApi.class, c);
				return (X) authApi;
			}
		});
		
		
		Mockito.when(authApi.v1AuthenticateExtensionAppPost(Mockito.any())).thenAnswer(i -> {
			ExtensionAppAuthenticateRequest ar = (ExtensionAppAuthenticateRequest) i.getArgument(0);
			return new ExtensionAppTokens().appId("appid123").appToken(ar.getAppToken()).symphonyToken("Sym123");
		});
		
		
        this.mockMvc.perform(
        	get("/symphony-app/podAuth?podId=666")
        	.contentType(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(jsonPath("$.appId", containsString("appid123")))
    		.andExpect(jsonPath("$.tokenA", containsString("someAppId/")))
    		.andExpect(status().isOk());
    }
	
}
