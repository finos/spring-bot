package org.finos.springbot.teams.state;

import org.junit.jupiter.api.BeforeEach;

public class MemoryStateStorageTest extends AbstractStateStorageTest {

	@BeforeEach
	public void setup() {
		tss = new MemoryStateStorage();
	}
}
