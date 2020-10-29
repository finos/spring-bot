package org.finos.symphony.toolkit.spring.app.tokens.pod;

import com.symphony.api.model.ExtensionAppTokens;

/**
 * Handles the call to the pod to get the tokens to give to your application.
 * 
 * @author Rob Moffat
 *
 */
public interface PodTokenStrategy {

	public ExtensionAppTokens getTokens(String appToken, String podId) throws Exception; 
}
