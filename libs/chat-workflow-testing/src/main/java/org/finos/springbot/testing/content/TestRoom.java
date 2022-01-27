package org.finos.springbot.testing.content;

import org.finos.springbot.workflow.content.Chat;

public class TestRoom implements Chat {

	private final String key;
	private final String name;
	
	public TestRoom(String key, String name) {
		this.key = key;
		this.name = name;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

}
