package org.finos.symphony.toolkit.workflow.content;

import org.finos.symphony.toolkit.json.EntityJson;

public interface MessageParser {

	public Message parse(String sourceFormat, EntityJson entityJson) throws Exception;
}
