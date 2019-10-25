package com.symphony.spring.app.id;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;

import com.symphony.id.SymphonyIdentity;
import com.symphony.spring.app.AbstractTest;


@TestPropertySource(properties =  { "symphony.app.identity.location=file:target/some-test-id.json" })
public class NamedIDLocationTest extends AbstractTest {

	@BeforeClass
	public static void cleanUp() {
		deleteFile();
	}
	
	@Qualifier("appIdentity")
	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityGeneration() throws Exception {
		Assert.assertEquals("noAppId", id.getCommonName());
		Assert.assertTrue(new File("target/some-test-id.json").exists());
	}

	private static void deleteFile() {
		new File("target/some-test-id.json").delete();
	}
	
}
