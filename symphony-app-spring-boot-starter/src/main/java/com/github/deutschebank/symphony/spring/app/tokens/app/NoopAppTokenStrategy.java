package com.github.deutschebank.symphony.spring.app.tokens.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.api.id.SymphonyIdentity;

/**
 * Creates tokens but doesn't bother with the checks.  Totally unsuitable for 
 * external production use, but very handy for testing, since you can have your application running 
 * locally and on a server at the same time.
 * 
 * @author Rob Moffat
 *
 */
public class NoopAppTokenStrategy implements AppTokenStrategy {
	
	public static final Logger LOG = LoggerFactory.getLogger(NoopAppTokenStrategy.class);

	protected SymphonyIdentity appIdentity;
	
	public NoopAppTokenStrategy(SymphonyIdentity id) {
		this.appIdentity = id;
		LOG.warn("NoopAppTokenStrategy is being used: don't use this if running your app on a public pod");
	}
	
	@Override
	public String generateAppToken() {
		return appIdentity.getCommonName()+"/NoopToken";
	}

	@Override
	public void storeAppToken(String appToken, String podToken) {
	}

	@Override
	public boolean checkTokens(String appToken, String podToken) {
		return true;
	}

}
