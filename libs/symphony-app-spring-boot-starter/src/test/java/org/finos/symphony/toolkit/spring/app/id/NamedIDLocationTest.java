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


@TestPropertySource(properties =  { "symphony.app.identity.location=file:target/some-test-id.json" })
public class NamedIDLocationTest extends AbstractTest {

	@BeforeAll
	public static void cleanUp() {
		deleteFile();
	}
	
	@Qualifier("appIdentity")
	@Autowired
	SymphonyIdentity id;
	
	@Test
	public void testIdentityGeneration() throws Exception {
		Assertions.assertEquals("noAppId", id.getCommonName());
		Assertions.assertTrue(new File("target/some-test-id.json").exists());
	}

	private static void deleteFile() {
		new File("target/some-test-id.json").delete();
	}
	
}
