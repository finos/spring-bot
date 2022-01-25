package org.finos.symphony.toolkit.spring.app.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class PageControllerTest extends AbstractTest {
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Test
	public void testControllerLoad() throws Exception {
		
        this.mockMvc.perform(
        	get("/symphony-app/testgroup-controller.html")
        	.contentType(MediaType.TEXT_HTML))
        	.andDo(print())
        	.andExpect(content().string(containsString("<script type=\"text/javascript\" src=\"https://www.symphony.com/resources/api/v1.0/symphony-api.js\"")))
           	.andExpect(content().string(containsString("<script type=\"text/javascript\" src=\"http://localhost/symphony-app/starter-include.js\"></script>")))
           	.andExpect(content().string(containsString("const id = \"bob\";")))
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
           	.andExpect(content().string(containsString("const id = \"bob\";")))
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
           	.andExpect(content().string(containsString("const appId = \"bob\";")))
            .andExpect(content().string(containsString("const appName = \"Testular\";")))
    		.andExpect(status().isOk());
    }
}
