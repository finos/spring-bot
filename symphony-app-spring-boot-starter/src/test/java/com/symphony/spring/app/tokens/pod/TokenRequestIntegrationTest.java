package com.symphony.spring.app.tokens.pod;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.model.ExtensionAppTokens;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles({"inttest"})
@ContextConfiguration
public class TokenRequestIntegrationTest  {
	
	@Autowired
	ConfiguredPodTokenStrategy cpts;
	
	@Test
	public void doRequestToken() throws Exception {
		ExtensionAppTokens eat = cpts.getTokens("abc123", "dbtest");
		Assert.assertNotNull(eat.getSymphonyToken());
		Assert.assertEquals("symphony-practice-app", eat.getAppId());
		Assert.assertEquals("abc123", eat.getAppToken());
	}
	
}
