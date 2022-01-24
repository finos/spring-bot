package org.finos.symphony.toolkit.spring.app.id;

import java.io.File;

import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;

import com.symphony.api.id.SymphonyIdentity;

/**
 * This has the location set to something that can't exist, forcing us to create something somewhere else.
 * @author Rob Moffat
 */
@TestPropertySource(properties =  { 
		"symphony.app.identity.location=classpath:target/some-test-id.json", 
		"symphony.app.identity.commonName=crazyAppId"})
public class DefaultIDLocationTest extends AbstractTest {

	@BeforeAll
	public static void cleanUp() {
		deleteFile();
	}
	
	@Qualifier("appIdentity")
	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityGeneration() throws Exception {
		System.out.println(" id " +id.getCommonName());
		Assertions.assertEquals("bob", id.getCommonName());
		//Assertions.assertTrue(new File("./crazyAppId.json").exists());
		//deleteFile();
	}

	private static void deleteFile() {
		//new File("./crazyAppId.json").delete();
	}
	
}
