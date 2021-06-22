package org.finos.symphony.toolkit.spring.app.bundle;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class BundleTest extends AbstractTest {

	@Autowired
	protected MockMvc mockMvc;
	
	@Test
	public void testMarketBundleLoad() throws Exception {
		
        this.mockMvc.perform(
        	get("/symphony-app/secret/bundle.json")
        	.contentType(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(content().string(containsString("\"name\" : \"Testular\"")))
        	.andExpect(jsonPath("$.rsaKey", containsString("-----BEGIN PUBLIC KEY-----")))
    		.andExpect(jsonPath("$.loadUrl", containsString("http://localhost/symphony-app/testgroup-controller.html")))
    		.andExpect(status().isOk());
    }
	
	@Test
	public void testUrlParamBundleLoad() throws Exception {
        this.mockMvc.perform(
        	get("/symphony-app/bundle.json")
        	.contentType(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(jsonPath("$.applications[0].id", containsString("someAppId")))
    		.andExpect(jsonPath("$.applications[0].url", containsString("http://localhost/symphony-app/testgroup-controller.html")))
        	.andExpect(jsonPath("$.applications[0].name", containsString("Testular")))
        	.andExpect(status().isOk());
    }
	
}
