package org.finos.symphony.toolkit.spring.app;

import java.io.InputStream;
import java.util.Scanner;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.app.SymphonyAppConfig;
import org.finos.symphony.toolkit.spring.app.AbstractTest.LocalConfiguration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.json.SymphonyIdentityModule;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes={TestApplication.class, SymphonyAppConfig.class, LocalConfiguration.class, SymphonyApiConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(print=MockMvcPrint.LOG_DEBUG)
@WebAppConfiguration
public abstract class AbstractTest {
	
	@TestConfiguration
	public static class LocalConfiguration {
		
		@Bean
		public ObjectMapper objectMapper() {
			ObjectMapper om = new ObjectMapper();
			om.registerModule(new SymphonyIdentityModule());
			return om;
		}
		
	}
	
	@Autowired
    private WebApplicationContext wac;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    
    public static String asString(InputStream is) {
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}	
	}
}
