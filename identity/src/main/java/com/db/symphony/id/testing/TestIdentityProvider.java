package com.db.symphony.id.testing;

import java.io.File;
import java.io.FileInputStream;

import com.db.symphony.id.IdentityConfigurationException;
import com.db.symphony.id.SingleSymphonyIdentity;
import com.db.symphony.id.SymphonyIdentity;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides a symphony identity either from a system property or a file on the
 * filesystem. System property must consist of a JSON-serialized
 * {@link SingleSymphonyIdentity}, with the name "symphony-test-identity".
 * 
 * File must be called "symphony-test-identity.json" and be either in the
 * current working directory or any ancestor.
 * 
 * @author Rob Moffat
 *
 */
public class TestIdentityProvider {

	public static final String TEST_IDENTITY_PROPERTY = "symphony-test-identity";

	public static SymphonyIdentity getTestIdentity() {
		return getIdentity(TEST_IDENTITY_PROPERTY);
	}

	public static SymphonyIdentity getIdentity(String name) {
		try {
			ObjectMapper om = new ObjectMapper();
			String property = System.getProperties().getProperty(name);
			if (property != null) {
				return om.readValue(property, SymphonyIdentity.class);
			} else {
				File f = getSymphonyPropertiesFile(name);
				return om.readValue(new FileInputStream(f), SymphonyIdentity.class);
			}
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't load test identity "+name, e);
		}

	}

	public static File getSymphonyPropertiesFile(String name) {
		// ok, hunt for a default
		File currentDir = new File("").getAbsoluteFile();
		while (currentDir != null) {
			File x;
			if ((x = new File(currentDir, name + ".json")).exists()) {
				return x;
			}
			currentDir = currentDir.getParentFile();
		}

		// no default found. Throw exception.
		throw new IdentityConfigurationException("Was expecting to find a " + name
				+ ".json file in the current directory or a parent, or " + name + " system property", null);
	}
}
