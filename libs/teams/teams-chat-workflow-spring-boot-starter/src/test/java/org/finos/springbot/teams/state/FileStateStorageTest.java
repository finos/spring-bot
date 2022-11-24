package org.finos.springbot.teams.state;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { MockTeamsConfiguration.class, DataHandlerConfig.class })
@ActiveProfiles("teams")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileStateStorageTest extends AbstractStateStorageTest {

	@Autowired
	EntityJsonConverter ejc;

	private String tmpdir = System.getProperty("java.io.tmpdir") + File.separator + "file-test" + File.separator;;

	@BeforeEach
	public void setup() throws IOException {
		cleanUp();
		Path path = Paths.get(tmpdir);
		if (Files.notExists(path)) {
			Files.createDirectory(Paths.get(tmpdir));
		}
		this.tss = new FileStateStorage(ejc, tmpdir);
	}

	@AfterEach
	public void cleanUp() throws IOException {
		Path path = Paths.get(tmpdir);
		if (Files.exists(path)) {
			FileUtils.deleteDirectory(new File(tmpdir));
		}
	}

}
