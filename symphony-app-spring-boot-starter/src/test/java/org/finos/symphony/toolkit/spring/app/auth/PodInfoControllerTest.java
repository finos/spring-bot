package org.finos.symphony.toolkit.spring.app.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfo;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfoStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

public class PodInfoControllerTest extends AbstractTest {

	@MockBean
	PodInfoStore store;
	
	@Test
	public void testRegistration() throws Exception {
		
		String podInfo = asString(PodInfoControllerTest.class.getResourceAsStream("/pods/9999.json"));
		
		this.mockMvc.perform(
	        	post("/symphony-app/podInfo")
	        	.header("Secret-Key", "secret")
	        	.content(podInfo)
	        	.contentType(MediaType.APPLICATION_JSON))
	        	.andDo(print());
		
		ArgumentCaptor<PodInfo> args = ArgumentCaptor.forClass(PodInfo.class);
		
		Mockito.verify(store).setPodInfo(args.capture());
		
		List<PodInfo> vals = args.getAllValues();
		Assertions.assertEquals(1, vals.size());
		Assertions.assertEquals("9999", vals.get(0).getCompanyId());
		Assertions.assertEquals("cert-app-auth-example", vals.get(0).getAppId());
		Assertions.assertEquals("https://your.pod.domain:8444/sessionauth", vals.get(0).getPayload().getSessionAuthUrl());
		Assertions.assertEquals("appEnabled", vals.get(0).getEventType());
		
	}
	
	@Test
	public void testFailedRegistration() throws Exception {
		
		String podInfo = asString(PodInfoControllerTest.class.getResourceAsStream("/pods/9999.json"));
		
		this.mockMvc.perform(
	        	post("/symphony-app/podInfo")
	        	.header("Secret-Key", "sdf")
	        	.content(podInfo)
	        	.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}
}
