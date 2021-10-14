package org.finos.symphony.toolkit.spring.app.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.finos.symphony.toolkit.spring.app.tokens.app.AppTokenStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.symphony.api.login.AuthenticationApi;


/**
 * Pod Auth is where we look up the details of a pod, and make a request back to a pod.
 * Pods can be looked up from stored details, or from 
 *   
 * 
 * @author Rob Moffat
 *
 */
public class AppAuthControllerTest extends AbstractTest {

	@MockBean
	AppTokenStrategy ats;
	
	@MockBean(name="someNewBean")
	AuthenticationApi authApi;
	
	@Test
	public void testAuthentication() throws Exception {
		
		Mockito.when(ats.checkTokens(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
			
		
        this.mockMvc.perform(
        	get("/symphony-app/appAuth?podToken=abc123&appToken=456xyz")
        	.contentType(MediaType.APPLICATION_JSON))
        	.andDo(print())
    		.andExpect(status().isOk());
    }
	

	@Test
	public void testAuthenticationFail() throws Exception {
		
		Mockito.when(ats.checkTokens(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
			
		
        this.mockMvc.perform(
        	get("/symphony-app/appAuth?podToken=abc123&appToken=456xyz")
        	.contentType(MediaType.APPLICATION_JSON))
        	.andDo(print())
    		.andExpect(status().isUnauthorized());
    }
	
}
