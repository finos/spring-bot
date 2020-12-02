package org.finos.symphony.toolkit.koreai.spring;

import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;

public interface KoreAIBridgeFactory {

	/**
	 * Constructs the bridge for a given {@link KoreAIInstanceProperties}, which defines the
	 * mapping between symphony bot and koreAI bot.
	 */
	SymphonyStreamHandler buildBridge(KoreAIInstanceProperties props, ApiInstance symphonyAPI);
	
}
