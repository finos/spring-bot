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

/**
 * This has the location set to something that can't exist, forcing us to create something somewhere else.
 * @author Rob Moffat
 */
@TestPropertySource(properties =  { 
		"symphony.app.identity.location=classpath:target/some-test-id.json", 
		"symphony.app.identity.commonName=crazyAppId"})
public class DefaultIDLocationTest extends AbstractTest {

	@BeforeClass
	public static void cleanUp() {
		deleteFile();
	}
	
	@Qualifier("appIdentity")
	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityGeneration() throws Exception {
		Assert.assertEquals("crazyAppId", id.getCommonName());
		Assert.assertTrue(new File("./crazyAppId.json").exists());
		deleteFile();
	}

	private static void deleteFile() {
		new File("./crazyAppId.json").delete();
	}
	
}
