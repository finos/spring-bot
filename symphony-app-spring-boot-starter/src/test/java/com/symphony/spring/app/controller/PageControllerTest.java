package com.symphony.spring.app.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.symphony.spring.app.AbstractTest;

public class PageControllerTest extends AbstractTest {
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Test
	public void testControllerLoad() throws Exception {
		
        this.mockMvc.perform(
        	get("/symphony-app/controller.html")
        	.contentType(MediaType.TEXT_HTML))
        	.andDo(print())
        	.andExpect(content().string(containsString("<script type=\"text/javascript\" src=\"https://www.symphony.com/resources/api/v1.0/symphony-api.js\"")))
           	.andExpect(content().string(containsString("<script type=\"text/javascript\" src=\"http://localhost/symphony-app/starter-include.js\"></script>")))
           	.andExpect(content().string(containsString("const id = \"someAppId\";")))
            .andExpect(content().string(containsString("Testular Controller")))
    		.andExpect(status().isOk());
    }
	
	@Test
	public void testPageLoad() throws Exception {
		
        this.mockMvc.perform(
        	get("/symphony-app/starter-app-page.html")
        	.contentType(MediaType.TEXT_HTML))
        	.andDo(print())
        	.andExpect(content().string(containsString("<script type=\"text/javascript\" src=\"https://www.symphony.com/resources/api/v1.0/symphony-api.js\"")))
           	.andExpect(content().string(containsString("const id = \"someAppId\";")))
           	.andExpect(content().string(containsString("const inServices =  [\"modules\",\"applications-nav\",\"ui\",\"share\",\"extended-user-info\"];")))
            .andExpect(content().string(containsString("My First Symphony App Page")))
    		.andExpect(status().isOk());
    }
	
	@Test
	public void testJsLoad() throws Exception {
		
        this.mockMvc.perform(
        	get("/symphony-app/starter-include.js"))
        	.andDo(print())
        	.andExpect(content().string(containsString("\"http:\\/\\/localhost\\/symphony-app\\/starter-app-page.html\"")))
           	.andExpect(content().string(containsString("const appId = \"someAppId\";")))
            .andExpect(content().string(containsString("const appName = \"Testular\";")))
    		.andExpect(status().isOk());
    }
}
